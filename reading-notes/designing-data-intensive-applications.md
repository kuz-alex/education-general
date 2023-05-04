
# Chapter 3. Storage and Retrieval

Main ideas:
- OLTP (made for transaction processing)
  - Relational versus log-structured storage
    - "Relational" updates in place, keeping only one copy of each record (B-trees)
    - "Log-based" writes sequentially, but requires multiple data structures and compression for obsolete copies of records.
- OLAP (made for analytics)
  - Analytic workloads are a special case with their own optimizations.


## Data Structures That Power Your Database

- The chapter starts with an example of the database storage implemented as append-only data file (log). Many real databases use this kind of format.
  - When we write an entry to the database, we just append it to the file. Perfomance for `set` operation is very good, appending to a file is efficient.
  - When we want to read an entry, we search the whole log file for the key. This has terrible performance (linear `O(n)`).
- To solve performance on reads, we can utilize indexes, which we can be can generalized as some additional metadata on the side that helps us locate data.
  - The problem with indexes is that they speed up read queries, but every index slow down writes. This why you as a developer should choose indexes manually.

We focus only on reads and writes, but real databases solve more problems, such as:
  1. Concurrency control
  2. Reclaiming disk space so that the log doesn't grow forever.
  3. Handling errors and partially written records.

### Hash Indexes
- To implement indexes we use a in-memory hash map, where every key is mapped to a byte offset in a data file.
  - When writing a new key-value pair to the file, you also update the hash map to reflect the offset of the data you just wrote.
  - When reading a value, use a hash mash to find the offset in a data file and read the value.
- It's a viable approach, but all the keys should fit in RAM, since the hash map is kept in memory.
- So far, we've only been adding to a file, but we don't want it to grow forever and run out of disk space.
  - Break the log into segments when it reaches a certain size
  - Perform compaction where you eliminate duplicated keys
  - New merged segments are written to a new file, thus merging & compaction can be performed in the background thread.
  - Since we have multiple segments, our reads now are looking for indexes in every separate segment, from most recent to oldest.
    - Merging process keeps the numer of segments small, so there are fewer segments to search in.
- In a real implementation some of the problems are: file format, deleting records, crash recovery, partially written records, concurrency control.

### SSTables and LSM-Trees
- Hash map indexes has two major limitations: it must fit the whole hash map in memory and its range queries are not efficient.
- Make a change in the format of our segmented files: sequence of key-values pair is _sorted by key_. This is called _Sorted String Table_ or _SSTable_. This allows us to do range queries efficiently.
  - Since the entries are sorted we can omit indexing some of the entries. We can always find the unindexed entry in the middle of two indexed entries.
- Log Structured Merge-Tree uses SSTables with in-memory tree and this whole indexing structure works as follows:
  - We keep an in-memory balanced tree (e.g red-black tree), called a memtable.
  - When memtable gets bigger than a few megabytes - write it out to disk as SSTable file (sequence of sorted key-value pairs). The new SSTable file becomes the most recent segment.
  - Look ups for read operation become: memtable -> most recent on-disk segment -> other segments.
  - In the background there are two processes: merging and compaction process for segment files and submitting a memtable to a new file segment (with creating a new memtable).
- Performance optimizations:
  - Bloom filters to avoid querying for non-existing keys (which would require to scan memtable and every segment)
  - Size-tiered compaction: never and smaller SSTables are successively merged into older and larger SSTables.
  - Level-tiered compaction: _not sure if I understand this_

### B-trees
- SSTables break the database down into variable-sized _segments_ (several megabytes) and always write sequentially.
- B-trees break the database down into fixed-size _blocks_ or _pages_ (4KB traditionally) and read/write one page at a time. Each page can be identified using an address or location.
- Searching for a key:
  - You start at the page which is _root_ of the B-tree. Root page contains several keys and references to child pages.
  - Each child page contains sub-ranges
  - We go further into child pages until we get to a _leaf_ page, which either contains values or references to values for our key.
- Updating a value for an existing key: we search for the leaf page, change the value in that page and write page back to disk.
- Adding a new key: find the page whose range encompas our new key and add it. If there isn't enough space in the page, it's split into two half-full pages and the parent page is updated to account for two new pages (parent page gets two new references, so the depth of the tree doesn't increase).
- The tree remains balanced: a B-tree with n keys will have a depth of O(log n).
- Making B-tree reliable
  - Overwriting value in place and splitting a page into sub-ranges is dangerous, they can be interrupted by a crash resulting in an inconsistent  state.
  - Keep a _write-ahead log_ (WAL, _redo log_). Append-only file that keeps record of every B-tree modification. Before applying modification to the actual pages, we record it to WAL. After a crash we can restore the state from this file.
  - Two threads might access B-tree at the same time. Use _latches_ (lightweight locks).
- B-tree optimizations: abbreviating keys (especially those that only act as a boundaries between ranges), keeping leaf pages in sequential order on disk, keeping pointers of left and right sibilings of a page.

### Comparing B-Trees and LSM-Trees
- General rule: LSM-trees are faster for writes, B-trees are faster for reads. LSM-trees slower in reads because they have to go through the memtable and all the segments (SSTables).
- Advantages of LSM-trees:
  - In write-heavy applications LSM-trees are generally better because they write compact SSTables rather than B-trees which are overwriting several pages in the tree even for a single change.
    - LSM-trees are considered to have lower _write amplification_ (one write to database resulting in multiple writes to disk).
  - Can compress better and produce smaller files on disk than B-trees (B-trees leave some disk spce unused due to fragmentation).
- Downsides of LSM-trees
  - Compaction process can interfere with read/write performance. Disk have limited resources and it can happen that a request needs to wait for finish of some expensive compaction.
  - Compaction can also affect performance because disk write bandwidth needs to be shared between the flushing a memtable to disk and the compaction threads running in the background.
    - It can happen that compaction can't keep up with the rate of upcoming writes, the number of segments (SSTables) will grow which will affect performance of reads as well. You should explicitly monitor this.
  - Having multiple copies of the same key (in segments) is a disadvantage because it's harder to implement locks (transaction isolation). In B-trees, those locks can be attached directly to the tree (on ranges of keys).

### Other Indexing Structures
- __Storing values within the index__. The key in an index is the thing we search for, but the value can be either a link to a row elsewhere or an actual row.
  - _Heap file_ approach, each index keeps a reference to a row that is stored in _heap file_. Allows us to avoid duplication when multiple secondary indexes are present (because each index references a location).
  - _Clustered index_ approach, each index stores the row directly within.
  - Example: in MySQL's InnoDB storage engine, the primary key of a table is always a clustered index and secondary indexes refer to the primary key (rather than a heap file location).
  - _Covering index_ or _index with included columns_ are compromise between clustered and nonclustered indexes. It stores some of a table's columns within the index.

- __Mutli-column indexes__. Querying multiple columns of a table simultaneously.
  - _Concatenated index_ (like a paper phone book, concatenated keys like `lastname firstname`, good for finding people for a particular lastname, useless for searching for firstnames.
  - _Multidimensional (spatial) indexes_, often used for geographic locations (restaurants for certain latitude and longtitude). Or searching for products in a certain range of colors. Implemented with R-trees.

#### In-memory databases
- For example Memcached, which is intented for caching use only, that is it's acceptable if data is lost if machine is restarted.
- Performance advantage is not due to the fact that they don't read from disk (disk-based storage engine also doesn't read from disk when you have enough memory, OS caches blocks in memory). Rather, they can be faster because they avoid data encoding.
- They also allow more flexibility in data structures, for example Redis allows using priority queue and sets, and implementation is simplier because all the data is in memory.


## Transaction Processing or Analytics?

- In the early days, a write to the database usually meant a commercial transaction. Databases expanded into different areas, but word transaction nevertheless stuck, referring to a group of reads and writes.
- A typical usage pattern known as _online transaction processing_ (OLTP): look up a small number of records by some key, using an index and update them based on the user's input.
- But for data analytics pattern is different (it's called _online analytic processing (OLAP)_: reads across large number of records and calculate aggregate numbers (sum of revenue).

### Data warehousing
- Usually OLTP systems are critical for our businesses and we don't want to let business analyst run ad hoc analytic queries on them.
- _Data warehouse_ is a separate database that is read-only copy of the data in all the various OLTP systems.
- _Extract-Transform-Load_ process is a process of extracting, transforming into a analyst-friendly schema, cleaning up and loading data into the warehouse.
- Data warehouses are optimized for analytics, we will explore storage engines that are optimized for hits.

### Stars and Snowflakes: Schemas for Analytics
- In analytics there are less diversity of data models.
  - _Star schema (dimensional modeling)_: fact table at the center (_facts_ are individual events), usually with lots of columns.
    - Some of the columns of the fact table are attributes, other are references to other tables called _dimension tables_.
    - Table relationships are visualized as a star, with fact table in the middle surrounded by dimension tables.
  - _Snowflake schema_, a variation of template above, where dimensions are further broken into subdimension.

## Column-Oriented Storage

- For trillions of rows and petabytes of data in fact tables it's a challenge to optimize the data warehouse.
- We can take advantages of the fact that usually analysts don't do `SELECT *` on fact tables but query only for 4-5 columns at a time. We want to avoid reading all this rows, parsing them and filtering out needed columns. Thus, we will store columns of data together, instead of storing records (rows) together.
- Columns from the same fact table should store all rows in the same orders.


# Chapter 4. Encoding and Evolution

JSON, XML, Protocol Buffers, Thrift and Avro are all formats for encoding data. This chapters explores:
1. How they handle schema changes and backward/forward compatibility.
2. How each of these formats is used for for data storage and communication (web services, REST, RPC).

- Applications change over time and we should build systems where it's possible to adapt to change.

- Data models change too, relational databases assume that all data in the database conforms to one schema. Schema-on-read (schemaless) databases don't enforce a schema, so the database can contain a mixture of older and newer data written at different times.

When a data format or schema changes, application code also needs to be updated:
  - Server side applications: rolling upgrade (staged rollout), which is deploying a new version to a few nodes at a time and checking if everything's running smoothly. This enables upgrade without service downtime.
  - Client side applications: you're at the mercy of the user, they have to install updates, which may not happen for some time.

This means old and new versions of the code may potentially coexist and our systems needs to maintain _backward_ and _forward_ compatibility. Forward compatibility can be tricker, it requires older code to ignore additions made by a newer code.


## Formats for Encoding Data
- There are two ways programs work with data:
  1. In memory: objects, structs, lists, arrays, hash tables, trees, etc. They're all optimized for manipulation by CPU.
  2. When you write the data to a file or send it over the network, you have to encode it so other programs can understand it.

- Translation from in memory to a byte sequence, called encoding (_serialization, marshalling_). The reverse coalled decoding (_parsing, deserialization, unmarshalling_).

### Programming languages built-in encoding
We could use built-in support for encoding in a programming language (e.g java.io.Serializable), but this would bring the following problems:
  - Commitment, It ties you to a particular programming language for a long time. Other system in different languages would not be able to parse your data.
  - Security, decoding instantiates arbitrary classes, attackers can exploit that to execute arbitrary code.
  - Neglect of forward/backward compatibility, implementations are meant for quick and easy encoding.
  - Not efficient (Java's built in serialization is notorious for bad performance and bloated encoding).

### JSON, XML and CSV formats
  - XML and CSV encodings of numbers, you cannot distinguish between a number and a string that consist of digits. JSON doens't distinguish between integers and floaing point numbers. The JSON returned by Twitter API includes tweet's ID twice, sending it as a decimal string to work around the fact that JavaScript apps couldn't parse JSON number.
  - JSON and XML supports Unicode character strings (human readable text), but don't support binary strings (sequences of bytes). A way to get around that limitation is to encode data as text using Base64, the schema is used then to interpret values as Base64-encoded.
  - There are optional schemas for both XML and JSON (XML is more widespread). They're complicated to learn and implement. Applications that don't use these schemas would need to hardcode the appropriate encoding/decoding logic when working with such data.
  - CSV don't support schemas, applications handle changes manually. CSV is also quite vague, not every parser correctly implements the specifications for CSV escaping.

Main advantage of using these formats is that they're popular and can be used to interchange data between organizations, without going through extra agreements and making conventions.


### Binary encoding
For data inside an organization these are viable. When thinking about terabytes of data, the choise of data format can have a big impact.

- JSON takes less space than XML and has different variants of binary encodings. E.g MessagePack, which brings a small reduction in size and loss of human-readability. It's not clear if it's a good trade-off. There are better binary encodings.

- __Apache Thrift__
  - Developed at Facebook
  - Requires a schema for any data that is encoded. Brings field type annotations and length indication.
  - Uses field tags to omit sending field names (e.g "1" for "firstName", "2" for "secondName"). JSON binary formats always send the key's names as well, increasing the size.
  - Has two different formats: _BinaryProtocol_ and _CompactProtocol_. Latter brings additional compaction strategies:
    - packing the field type and tag number into a single byte
    - variable-length integers

- __Protocol Buffers__
  - Developed at Google
  - Similar to Thrift CompactProtocol, just does the bit packing slightly differently.
  - They don't have a list or array datatype, but use `repeated` marker for fields (third along `required` and `optional`).

- Schema evolution for the above two encodings:
  - Easy to add new fields (with new tag numbers).
    - Forward compatibility: old code will simply ignore new fields, datatype annotation will tell the parser how many bytes to skip.
    - Backwards compatibility: new code will be able to read old data as long as we don't change tag numbers. Only detail is that every new field cannot be "required", it must be optional and provide a default values.
  - Removing field, for compatibility you can only remove optional fields and never reuse the tag numbers.
  - Protocol buffers also enabled change from `optional` to `repeated` (evolution from single-valued to multi-valued), new code reading old data sees a list with zero or 1 element, old code reading new data sees only the last element of the list. Thrift doens't support that, but it has the advantage of supporting nested lists.

- __Apache Avro__
  - Subproject of Hadoop
  - Doesn't use `optional` and `required` markers (it has unions (`{null, string} field`) and default values inastead)
  - Uses a schema to specify the structure of the data being encoded. Two schema languages: one for humand editing, one that is more easily machine readable.
  - There are no tag numbers in the schema, there are also nothing to identify fields or their datatypes. The encoding simply consist of values concantenated together. Thus it's very compact.
    - When parsing the binary data, you're reading fields (and their types) in the exact order they appear in the schema. Any mismatch would mean incorrectly decoded data.
- How Avro support schema evolution?
  - When app wants to encode the data, it encodes it using whatever version of the schema it knows about (e.g the schema that is currently compiled into application). It's the `writer's schema`.
  - When app wants to decode the data, it expects the data to correspond to a schema, this is the `reader's schema`.
    - Writer and reader schemas don't have to be the same. They only have to be compatible. When the data is parsed, Avro resolves the differences, translates data from the writer's schema into reader's schema.
  - For compatibility, in Avro you may only add or remove a field that has a default value. When using a new schema we read a record written with an older schema, the default value is filled in for the missing field.

How does the reader know the writer's schema with which a particular piece of data was encoded (reader needs that to resolve the differences):
  - _Large file with lots of records_, a very common case, especially in the context of Hadoop (all records in a file encoded with the same schema. Writer of that file can just include the writer's schema in the beginning (Avro object container file).
  - _Database with individually written records_, where you cannot assume all the records will have the same schema. Include a version number at the beginning of every encoded record and keep a list of schema versions in the db. A reader can fetch a record and lookup the writer's schema.
  - _Sending records over a network connection_, two processes can negotiate the schema version at the setup step of the connection and use it for the lifetime of the connection (Avro RPC protocol).


- Avro is good for _dynamically generated_ schemas. E.g you have relational database whose contents you want to dump to a file.
  - This is due to the fact Avro's schema doens't have any tag numbers. If you were using Thrift or Protobuf, the field tags would likely have to be assigned by hand and every update you would need to manually upate the mapping from database column names to field tags.

- Avro provides optional code generation, thus we can omit generating code for dynamically types languages like Python, Javascript or Ruby. Moreover for dynamically generated schema, such as object container file from the database dump, code generation is an unnecessary obstacle to getting to the data. You can just open the file and look at the data with schema in it's metadata.

- So, Protobufs, Thrift and Avro all use a schema to describe a binary encoding format. Their schemas much simplier and support more features than JSON/XML schemas.
  - Even though textual data formats (JSON/XML/CVS) are widespread, binary formats have their own benefits:
    - They're more compact
    - Schema is a valuable form of documentation, it's maintained automatically since a schema is required for decoding.
    - Keeping versions of schemas allows checking for forwards and backwards compatibility before deploying.
    - For statically typed PL the schemas are usefull with it's code generation, type-checking at compile time.
  - Schema evolution allows the same kind of flexibility as schemaless JSON database provide, while also providing more checks for your data and better tooling.

## Modes of Dataflow

- Ways that data flows from one process to another:
  - Databases
  - Service calls
  - Async message passing

### Dataflow Through Databases
- Backward compatibility: your future self needs to be able to read what you previously wrote.
- Forward compatibility: value in the database may be written by a _newer_ code and subsequently read by an _older_ code. This may happen in a rolling upgrade, when some instances already updated and write the new version of data, while some running and old version that doesn't know anything about new format.
- Data might be lost when an older version of the application reads the record, updates it and writes it back. The encoding formats support such preservation of unknown fields.

- Databases contain a lot of older records that were written with an older schema (_data outlives code_). Rewriting (migrating) old data into new schema is very excensive and db's prefer not to do that.
  - You still might want to dump the contents of the database (as for archival purposes), in this case you can encode all the data with the new schema consistently. Avro object containers are usually a good fit for that (or in analytic-friendly column-oriented format such as Parquet).

### Dataflow Through Services: REST and RPC

Web services are used in the following contexts:
1. Client application making HTTP requests.
2. Service making requests to another service located within the same datacenter.
3. Services from different organizations communicating through API.

- REST
  - Design philosophy that builds upon HTTP principles.
  - Uses URLs to request resources and utilizes HTTP features such as methods, headers, etc for request specifications.

- SOAP
  - XML-based protocol for making network API requests.
  - Independent from HTTP, doesn't use its features. Has its own standards (web service framework _WS-*_).
  - API is described using an XML-based language WSDL (web service description language).
    - WSDL provides code generation, creates classes and methods (encoded to XML and decoded by framework)
    - WSDL not human readable, you need to generate code or use IDE tools to understand it.
    - Different vendors' implementations still cause problems, even tho WSDL good has standards.

There also were RPCs (remote procedure calls) before web services.

- Problems with RPCs
  - RPC tries to make a network request look like a local method call, but it has problems, because request is different from a local call in the following ways:
    - Network request can fail and you should be able to anticipate it by retrying. But local function calls are predictable (we know beforehand if they succeed or fail).
    - Local function either returns a result, or throws an exception, or never returns. A network request has extra outcode: _timeout_. When you don't get a response from a remote service - you don't know if your request ever reached it.
    - If you retry are failed request, it could happen that previous requests were getting through and only responses were getting lost. Local function don't have this problem. In requests you might need to build mechnism for deduplication (idempotence).
    - Requests predictably take time to execute.
    - You can pass pointers to a local function, in a request all the data you pass should be encoded before sent over the network.
    - Client and server can be written in different programming languages with different datatypes, so RPC must do additional translation of datatypes.
  - So, there's no point in trying to make a remote service look like a local objects in your programming language. They're fundamentally different things.

- Current directions for RPC
  - Thrift Avro comes with RPC support included. gRPC is an implementation using Protocol Buffers.
  - New implementations are more explicit about the fact that remote request is different from local functions. Finagle and Rest.li use _futures_, gRPC support _streams_. They all convenient in different ways.
  - RPC protocols with binary encodings can achieve better performance thna something like JSON over REST.
    - RESTful APIs are still more convenient for debugging, development and experimentation. For these reasons REST still dominant.
    - RPC is usually used within the same organization.

- Data encoding for RPC
  - Backward compatibility for requests, old clients should be able to ask an updated server.
  - Foward compatibility for responses, so new clients don't break when served from an older server.
  - Compatibility for RPC scheme is inherited from whatever encoding it uses:
    - Thrift, gRPC (Protocol Buffers), and Avro RPC are evolved in according with their encoding format.
    - SOAP uses XML schemas, these can be evolved, but there are some pitfalls
    - RESTful APIs use JSON, new url parameters or new response fields can be added safely.

### Message-Passing Dataflow

- We've looked at RPC (data flows between processes) and databases (one process encodes data and another process reads it). There also are async message-passing systems, which are somewhere in between.
- Typically client's request in these systems are called _message_, like RPC they're sent with low latency, but not via direct network connection (like dbs).
- _Message broker_ (or _message queue_) has the advantages over the RPC:
    - Can act as a buffer when recipient is unavailable
    - Redelivers messages to a crashed process
    - It allows one message to be sent to several recipients
    - It doesn't need to know port and IP address of the recipient
    - Decoupling, sender just publishes messages without care who consumes it
- The other difference from RPC is that sender doesn't expect a response to it's messages (it's possible only via separate channel).
- Modern open source message broker implementations:
    - RabbitMQ, ActiveMQ, HornetQ, NATS and Apache Kafka
- Message broker works as follows:
    - _A message_ is dispatched to a _queue_ or a _topic_, and the broker ensures it's delivery to one or more _consumers_ or _subscribers_ to that queue.
    - To imitate a reply, we might have a reply queue, in which consumers can publish their own messages.
- Since message brokers don't enforce a format, you can use any encoding and have backward and forward compatibility, so you'll be able to change publishers and consumers however you like.
- __Distributed actor frameworks__
    - _Actor_ is a programming model for concurrency in a single process. An _actor_ is encapsulated logic which may have a local state. It represents one client or entity. It sends and received asynchronous messages, delivery is not guaranteed: messages can be lost. Actor processes a single message at a time, thus it doesn't worries about threads.
    - _Distributed actor frameworks_, is a programming model which is used to scale application across multiple nodes. The same message-passing mechanism is used (even for two different nodes over the network). If the message is sent over the network, it's encoded into a byte sequence and decoded on the other side.
        - Actor model already assumes that messages may be lost (even within the single process), so this model is more natural to work with than RPC.
    - Distributed actor frameworks basically integrate message broker and the actor programming model into a single framework.
    - Popular frameworks and how they handle data encoding:
        - _Akka_ uses Java's built-in serialization (no `-> <-` compatibility), you cna replace it with Protocol Buffers and gain the ability to do rolling upgrades.
        - _Orleans_, it uses a custom encoding format which also doesn't support rolling upgrades from the box. Custom serialization plugins can be used.
        - _Erland OTP_.


# Chapter 5. Replication

- Keeping a copy off the same data on different nodes, potentially in different locations.

## Leaders and Followers
- Replication is keeping a copy of the same data on multiple machines.
    - Possible to store data close to users geographically.
    - When a single machine fails, the system keeps going.
    - To scale out the number of machines that can serve read queries.
- Main challenge is to keep everything in sync when data changes (if data doesn't change, replication is simple just copy data to every node).
    - To keep data in sync, we use the following approaches: single-leader, multi-leader and leaderless.
- Leaders and followers
    - Each node with the database copy is called a replica. How to ensure that every replica contains the same data? Each write should be known to every replica.
    - Leader-based replication (active/passive, master/slave) is most common apprach where one node acts as a leader.
    - Leader node handles write requests and sends data to every other node through replication log or change stream.
    - Follower nodes process the log from the leader and updates its copy of the data. Follower nodes also server read requests.
    - This approach is built-in in Postgres, MySQL, etc. It also used in non-relational databases. Not only databases, but message brokers too, such as Kafka and RabitMQ highly available queues.
- Sync vs async replication
    - When a user sends a data update request, first it gets to the leader, leader then forwards it to the followers. Eventually leader notifies the client that their request was successful.
- In synchronous replication the leader waits for the client to repond making sure the change was saved and only then makes it visible to the client.
    - Because of that we always know that there's a copy of data saved on some node. And if the leader fails, we will be able to recover.
        - There's a problem with that if a synchronous follower doesn't response, write gets blocked, can't be processed.
    - For that reason, usually only one node is synchonous and if it becomes slow or fails - one of the async nodes can take it's place.
    - So, leader and the synchronous follower will have an up-to-date data.
- In asynchronous replication the leader sends a message but doesn't wait for a reponse.
    - If the leader fails we cannot recover data, any writes that have not yet been replicated are lost (even though we sent "success" to the client).
    - So it's a trade-off: the write cannot be guaranteed to be durable. But async replication is still widely used, esp if there are many followers and they geographically distributed.
    - There are research on replication and for critical data in asynchronous cases: look up approaches like _chain replication_.
- How do you set up a new follower (e.g to replace a fallen node)?
    - You cannot simply copy done it because there are always lots of incoming writes to the db. And you don't wanna lock the entire database to block writes either.
    - 1. You take a snapshot of the leader's database at some point in time (without locking the db).
    - 2. Copy the snapshot to the new follower node.
    - 3. Follower connects to the leader and requests all the changes that were made to the db since the snapshot was taken.
    - 4. The follower catches up when he proceeds all the changes from the leaders backlog. It can now proceed new changes from the leader as they happen.
### Handling Node Outages
- We need to be able to reboot individual nodes without downtime (because a node can fail or shut down due to maintainance). The system should be able to keep running, a single node impact should be as small as possible.
- To recover as a follower node, we keep a log of the data changes from the leader. When a node gets back online, it can ask the leader for all the transacations which happened when it was disconnected. It then applies all the transactions and catches up with the leader.
- Leader failover is more complicated, the new leader should be choosen, the followers should be reconfigured, the clients should start sending data to a new leader. It’s usually handled with the following steps:
    - To determine that the leader had failed we can simply use a timeout: all the nodes are bouncing back and forth and if a particular node doesn’t respond - it’s considered to be dead (that is unless taken down for maintainance).
    - To choose a new leader, we use a controller node, it’s usually a good idea to choose a most up-to-date replica as a new leader.
    - Clients and followers should be reconfiured to use a new leader.
- Challenges when handling a leader failure:
    - Data is lost because new leader might not receive all the transacations from the old leader. If the old leader later gets back, all the unprocessed data is usually discarded.
    - Discarding data can cause problems if for example you use Redis alongside your Postgres, data discarded in old leader might have been saved in Redis.
    - Two nodes might consider themselves to be a leader simultaneously and failover system can accidentally shut down both nodes.
    - A load spike can cause a node’s response time to increase above the timeout and an unnecessary failover might make things worse in this high load time.
### Implementation of Replication Logs
- Statement-based replication
    - Every INSERT, UPDATE or DELETE is forwarded to the followers, who parse and execute those statements. There are problems with this:
        - Nondeterministic functions as NOW() RAND() etc, they will generate different values on each replica.
        - Tables that use autoincrement, the data should be identical on all replicas for that.
        - Sometimes there might be side effects (triggers, stored procedures, user-defined functions).
    - The leader can replace any nondeterministic function call with a fixed return vaule, but there are many edge cases and other replication methods are preferred.
    - Statement-based replication was used in MySQL before 5.1, but now MySQL uses row-based replication.
- Write-ahead log (WAL) shipping
	- Storage engines usually keep a log with every write:
		- Log-structures storage engines (SSTables and LSM-Trees) use this log as a storage. Log segements are compacted and garbage-collected in a background.
		- B-tree overwrite every individual disk block, but it still keeps a WAL, because every write is first written to WAL so that index can be restored after crash.
	- In the same fasion we keep a WAL on the leader and it sends it over the network to the followers.
	- The problem with this approach is that our WAL describes the data on a very low level, so it's coupled to the storage engine.
	- We cannot do a db updgrade without downtime. If we weren't coupled to the db engine, then we would upgrade all the follower nodes and do a failover on the leader. 
    - This approach is used in PostgreSQL and Oracle. Downside of this approach is that WAL describes the data on the very low level and it makes replication closely tied to a storage engine. For this reason, if you want to upgrade the database, WAL might require mandatory downtime.
- Logical log (row-based) replication
	- Logical log allows us to decouple from the storage engine by keeping different formats for replication.
	- It sends records:
		- For inserting, record contains all the values of new columns.
		- For deleting it contains enough information to uniquely identify the row (usually a primary key)
		- For updating a record contains both the id and new data (at least for the columns that were modified).
	- When updating multiple rows, each row update will be represented as separate record.
	- This approach allows us to run different versions of DB and perform upgrades without downtime. It also more convenient for external systems that want to read the contents of the db, e.g data warehouses
- Trigger-based replication
	- In some cases you need more flexibility, e.g replicating only subset of data or doing some conflict resolution.
	- In those cases you can use triggers or stored procedures provided by databases to execute some custom code when the data changes.
	- This kind of replication more bug prone and has more overhead, but it's still sometimes useful due to its flexibility.

### Problems with replication lag
- Replication is a good way to tolerate node failures, other reasons for replication is scalability (multiple machines can handle more requests) and latency (serving geographically closer to the user)
- In leader based replication is usually all writes go through a single leader node, but read queries can be served from any replica. Application with mostly reads than writes are common pattern on the web.
- Adding more followers for serving read-only request realistically works only with asynchronous replication (otherwise a single node failure would make our whole system unavailable for writing).
- But if we're reading from an async. follower, we may see outdated information if the follower has fallen behind. This is called replication lag.
- Replication lag can be very small (and eventually the follower will catch up, which is called eventual consistency). But when our server is operating under high load or if the network is unstable the lag can increase to several seconds or even minutes.
- Problems that occur when replication lag happens:
    - Reading your own writes. E.g user edits his own profile and then refreshes the page to see the updates. With asynchronous replication the read request can be served from the stale replica and it would look like submitted data was lost. In this situation we need read-after-write (read-your-writes) consistency. Here's how to implement it in the leader-based replication:
        - If a user edits his own profile, we can configure the leader to serve read requests to the user for his own profile, so user always gets updated information.
        - When a user can change multiple things this approach won't work as most things would have to be read from the server (negating the benefit of read scaling). In this case we can track the time of the last update and, for one minute after the update, make all reads from the leader. You can also monitor the followers and prevent reads from the followers that are more than 1 min behind the leader.
        - The client can remember the logical timestamp of its most recent write - then the system can ensure that the request is served from the updated replica (outdated replica can bypass the request or wait til replica caught up).
    - There are more difficulties if your replicas are distributed across multiple DC, any request that needs to be served by the leader should be routed to DC that contains the leader.
    - Read-after-write consistency can be tricker if you account for multiple device (cross-device read-after-write).
- Monotonic reads.
	- When working with multiple replicas the followers are updated at different times. If a user refreshes the page, he can read a freshly written record from one machine and after another quick refresh he can ask this record from a machine where it doesn't yet exist. So it'll look like moving backward in time, you read something and on the second request it's not exist yet.
	- To avoid it we can make sure that a single user always reads from a particular replica, e.g we can select replica based on the hash of a user id. And when the node is down, only then we redirect user to a different replica. Monotonic reads is the guarantee that this anomaly won't happen.
- Consistent Prefix Reads
	- Imagine we have a dialog between users on a forum, a question and an answer. If an observer requests messages from two asynchonous followers, he may receive an answer (from updated replica) but not receive the actual question (bc another replica temporary outdated).
	- To solve this kind of anomaly we need a guarantee: consistent prefix reads. This guarantee says that if a sequence of writes happens in a certain order, then anyone reading those writes will see them appear in the same order.
	- This is a particular problem in sharded databases, there are ways to ensure consistency, but in many distributed dbs different shards operate independently.
	- To solve this we could ensure that related to each other writes are written to the same partition (not always possible to do efficiently). Or there are algorithms that explicityly track casual dependencies (will be later in the book).

### Multi-Leader Replication
This kind of replication is not really suited for a single datacenter (not much benefits), but there are scenarios where it makes sense:
- _Multi-datacenter opreation_. Within each datacenter a regular leader-follower replication is used; between datacenters, each datacenter's leader replicates its changes to the leaders in other DCs.
    - Performance. If we have multiple datacenters then it makes sense to put leader in each datacenter, so we don't lose benefits of DCs being closer to the users (a single leader in a particular DB would become a bottleneck for writes).
    - Tolerance of datacenter outages. When a leader fails each DC continues operate independently of the others, and replication catches up when the failed datacenter comes back online (in a single-leader configuration failover can promote a follower in another DC to be the leader).
    - Tolerance of network problems. Traffic between datacenters usually goes over public internet and less reliable than network within datacenter. Single-leader configurations are very sensible to those problems and multi-leader configurations have advantage.

The big disadvantage of a multi-leader replication is that multiple writes can be handled by different leaders and we need to perform additional confilict resolution. Also not all databases can handle autoincrements, triggers and integrity constraints with multi-leader replication. So when possible it's should be avoided.


# Chapter 9. Consistency and consensus

Глава начинается с описания консистентности, линеаризируемости и по нарастающей переходит к консенсусу.

До этого рассматривались гарантии консистентности для одного узла, но в этой главе это рассматривается для распределённых систем.

## Гарантии консистентности
- В распределённых датабазах всегда имюется неконсистентности (не важно какой метод был использован: single/multi/leaderless).
- Максимум что они могут гарантировать – это eventual consistency, но это слабая гарантия. С "eventual" консистетностью процесс разработки становится менее очевидным (не можем думать о датабазе как о переменных в коде).
- Существуют модели которые дают больше гарантий консистетности, мы рассматриваем в этой главе:
    - Линеаризуемость
    - Тотальный порядок
    - Консенсус

## Линеаризуемость
- С этим подходом, взаимодействие с несколькими дб узлами будет абстрагировано и мы взаимодействовать с базой будто у нас один узел.
- Пример нелинеаризуемой системы: сайт с результатами футбольного матча – из-за чтения из нескольких узлов можем в ответе получить 1-0, а на последующий запрос 0-0, вернувшись назад во времени.
- Линеаризуемая система: если однажды вернули новое значение, то на все остальные чтения тоже возвращаем новое значение.
- Линеаризуемость и сериализуемость – независимые понятия.

### Use-кейсы линеаризуемости
- Блокировки и выбор ведущего узла
- Ограничения и гарантии уникальности
- Межканальные временные зависимости

### Implementing linearizable systems
- Реплицируемые системы могут быть линеаризируемы след. образом:
    - Single-leader репликация – в определённых случаях. Не линеаризируема если используется снэпшот изоляция или асинхронные операции записи.
    - Consensus - да. Так устроены системы типа ZooKeeper и др.
    - Multi-leader репликация – нелинеаризируема (из-за потенциальных конфликтов).
    - Leaderless репликация - скорее всего нелинеаризируема, зависит от того как устроено решение конфликтов.
-  Потенциальные проблемы с линеаризируемом single-leader сетапом, в случаях когда ДЦ ведомого узла теряет соединение с ДЦ лидера. Причиной этому является то, что в линеаризируемой системе ведомые узлы должны синхронно отправлять read/writes в ведущий узел, который в другом ДЦ).
    - Одно из главных оснований для распределённых систем – географически близкие сервера для пользователей. И условно в ДЦ с лидером в России потеряет связь с ДЦ в другой стране, это downtime для пользователей из других стран.
- Теорема CAP - в любой системе, требующей линеаризуемости, если некоторые узлы становятся отключенными, то они должны ждать обработки запросов.  Системы, не требующие линеаризуемости, могут быть написаны так, что отключенным узлам не придется ждать.
    - CAP имеет важное значение исторически, но мало практической пользы, нужно быть осторожным с мисинтерпретацией.
- Линеаризуемость всегда даёт большую просадку в производительности. *Всегда*, а не только когда происходят задержки в сети. Даже оперативная память не является линеаризируемой, когда мы имеем дело с несколькими ядрами.

## Ordering Guarantees
- Существует связь между линеаризируемостью, порядком и консенсусом.  
- Causality - свойство, когда причина обязательно идёт перед следствием.  
    - Примеры причинности были приведены ранее: сообщение отправляется перед тем как его получают, вопрос приходит первее ответа.  
    - Системы которые соответствуют этому свойству - называются *causally consistent*: в момент чтения определённого ряда из датабазы, ты можешь прочитать все предшествующие ему ряды.
- В линеаризируемых системах порядок более строгий, чем порядок причинности (causal). В линеризируемых системах нет места параллельности. Все события можно представить так будто они упорядочены на одной хронологической прямой.
    - Но в causal порядке параллельность существует, например в таких системах как гит история изменений выглядит как граф порядковых зависимостей, когда есть возможность создать 2 параллельные ветки и мержить их потом между собой.
- Линеаризируемая модель проще для восприятия, но всегда несёт просадку в производительности. Causal consistency модель гораздо более производительна.
- Происходят исследования в поиске золотой середины между causal и linearizable моделями. Большинство систем не требует *полной* линеаризируемости.

### Sequence Number Ordering
- Трекать все причины и следствия – непрактично. Более простой способ определения порядка – трекать порядковые номера или временные метки.
- Логические порядковые номера могут гарантировать total порядок, т.к каждая операция имеет уникальный порядковый номер и мы всегда можем их сравнить.
    - Пример: Single-leader репликация хранит порядковые номера в своём логе репликации (replication log).
- Просто записывать временные метки недостаточно для установления причинности (causality). Существует решение, которое гарантирует причинность – временные метки Лампорта.
    - Это уникальные пары `(counter, node ID)`, которые каждый узел устанавливает на события.
    - Каждый узел и клиент знают про максимальное `counter` значение и включают это значение в каждый запрос. Если узел получит значение выше чем свой `counter`, он сразу увеличивает свой счётчик до этого значения.
- Временные метки Лампорта гарантируют тотальный порядок, но этого недостаточно для  решения многих проблем в распределённых системах.
    - Например, если 2 пользователя одновременно пытаются создать аккаунт с одним логином, то 2 ведомых узла должны *в моменте* решить какому пользователю отказать, а с метками Лампорта мы можем это решить только *после* того как мы сохранили эти 2 записи (т.к у нас будут обе метки для сравнения).

### Total Order Broadcast
- Это протокол для обмена сообщениями между узлами, он устанавливает следующие условия:
    - *Надежная доставка*, сообщения не теряются и если сообщение доставлено до одного узла, оно будет доставлено до всех остальных.
    - *Тотальный порядок доставки*, до каждого узла сообщение будет доставлено в одном порядке.
    - В случае с нестабильной сетью, алгоритм будет продолжать повторять запросы (чтобы доставить все сообщения в правильном порядке), пока сеть не будет починена.
- Можно думать об этом протоколе как о создании *лога*: доставка сообщения равняется аппенду в этот лог, каждый узел может читать лог и видеть один порядок событий. 
- Total order broadcast идеально подходит для имплементации репликации датабаз. Он так же полезен при имплементации сервисов блокировок (locks services).
- Total Order Broadcast имеет много сходств в линеаризуемостью. Но отличается тем, что в TOB один узел может отставать от остальных (задержки в доставке данных), но линеаризируемые системы предоставляют гарантию `recency guarantee`: каждое чтение гарантированно получит последнюю запись.
- **Имплементация линеаризации с помощью TOB**, пример с созданием профиля с уникальным логином:
    - Добавление записи с уникальным логином в журнал
    - Ожидание пока мы можем прочитать свою запись из журнала обратно
    - Поиск сообщения с записью уникального логина, если первый результат – твоё собственно сообщение, значит уникальный логин можно успешно бронировать.
    - Подобный алгоритм гарантирует линеризируемую запись, но не чтение (из-за возможных задержек, отсутствия `recency` гарантии).
- Так же возможна имплементация TOB через линеаризируемое хранилище. Порядковые номера реализуемые через линеаризируемое хранилище – это эквивалентно консенсусу.

## Distributed Transactions and Consensus
- Консенсус, простым языком, это когда несколько узлов приходят к одному решению.
    - *Выбор лидера* в single-leader репликации
    - *Atomic commit* все узлы приходят к одному решению при обработке транзакции.

### Atomic Commit and Two-Phase Commit (2PC)
- Для дб транзакций на одном узле, атомарность гарантирована тем, что сперва данные записываются на диск и только потом происходит *commit* записи. То что коммит происходит после успешной записи – это делает коммит атомарным.
- В распределённых системах недостаточно просто отправить commit запрос нескольким узлам т.к если некоторые узлы закоммитят транзакцию, а какой-то отменит, то появится неконсистентность.
- В Two-phase commit, процесс коммита и отмены транзакции разделён на 2 фазы и использует дополнительный сервис-координатор:
    - Координатор отправляет `prepare` запрос во все узлы и ждёт подтверждения обратно.
    - Если все узлы отвечают "да", то координатор отправляет `commit` запрос и на этом этапе происходит коммит. Если хотя бы один узел ответил "нет", то координатор отправляет `abort` запрос.
    - Координатор отправляет `commit` или `abort` запросы всем узлам бесконечно, пока все они однозначно их не получат. Т.е если один узел упал после ответа "yes", когда он поднимется, координатор убедится что он закоммитил транзакцию.
- В случае падения сервиса координатора, все узлы будут ждать пока он поднимется, чтобы получить запросы `abort/commit`. По этой же причине координатор должен сохранять своё решение отмены/коммита на диск, чтобы уметь продолжить работу, в случае падения при отправке решения.
    - Поэтому "commit point" для 2PC в итоге сводится к обычному атомарному коммиту на одном узле – координаторе.

### Distributed Transactions in Practice
- Имеют свои плюсы и минусы. Некоторые имплементацию несут большую цену по перформансу (транзакции могут быть в 10 раз медленнее чем транзакции одиночного узла).
- Есть несколько типов распределённых транзакций:
    - *Database-internal* – транзакции используя одинаковую софт имплементацию датабазы.
    - *Heterogeneous* – поддержка транзакций между датабазами от совершенно разных вендоров (значительно сложнее).
- *Exactly-once* обработка сообщений: в различных системах, сообщение из очереди сообщений может быть помечено как "processed" только если транзакция была успешно закоммичена. Это делается через атомарный коммит подтверждения и записи в одной транзакции.
    - Если подтверждение или запись не удаётся, то транзакция прерывается и mesage broker может переотправить сообщение позже.
    - Не должно быть сайд эффектов от транзакций (например отправка e-mail, если емейл сервер не поддерживает two-phase коммит протокол)
- Имплементация 2PC в разнородных технологиях: X/Open XA
    - Это API написанное на C для взаимодействия с координатором транзакций.
    - Координатор должен имплементировать XA API, обычно это просто библиотека которая загружается в один процесс с приложением создающим транзакции.
    - *Holding locks while in doubt*: координатор падает после отправки `prepare` – мы не хотим чтобы локи на базу сохранялись (они будут сохранены даже после рестарта)
    - Обычно координатор после сбоя может восстановить состояние из лог файла, но иногда непредвиденное случается и в системе могут появиться бесхозные транзакции. Решить такие транзакции можно путем ручного вмешательства.
    - Лимитации распределённых транзакций:
        - Координатор – это по сути та же дб, где хранится результаты транзакций, текущие имплементации координатора часто не fault-tolerant.
        - Координатор – это обычно простая библиотека и если он запускается на stateless сервере, то такой сервер перестаёт быть stateless. 
        - Координатор должен уметь работать с большим кол-вом систем данных, поэтому он не может определять дедлоки или учавствовать в SSI.
        - Во всех системах с 2PC –  сбой пусть на одном узле делает проблему гораздо серьезней, т.к из этого всегда следует сбой транзакции.

### Fault-Tolerant Consensus
- В общих чертах, консенсус – это когда несколько узлов *предлагают* значения и консенсус алгоритм *принимает* решение какое значение выбрать.
    - *Uniform agreement*: любые 2 узла приходят к одному решению
    -*Integrity*: узел решает только один раз
    - *Validity*: если узел пришёл к значению `v`, значит значение `v` было предложено каким-то узлом
    - *Termination*: каждый узел без сбоя рано или поздно выдвигает своё решение
- Самые широко известные алгоритмы консенсуса:
    - Viewstamped Replication (VSR)
    - Paxos
    - Raft
    - Zab
- Большинство алгоритмов консенсуса имплементированы, как Total Order Broadcast. TOB требует чтобы сообщения были доставлены до каждого узла в одном порядка ровно один раз. Это равносильно нескольким "раундам" консенсуса.
- В single leader репликации, ведущий узел создаёт условия консенсуса. Если на этом ведущем узеле происходит сбой, то мы должны выбрать нового лидера, для этого нам нужен ещё один этап консенсуса.
- Epoch numbering and quorums
    - Консенсус алгоритмы ведут монотонно возрастающий epoch номер и перед тем как лидер начинает что-то решать, он смотрит чтобы не существовал лидер с epoch номером выше его.
    - Для каждого лидера происходит 2 раунда голосования: один для выбора лидера, другой для принятия решения которое выдвигает лидер.
- Лимитации консенсуса
    - Голосование узлов за предложение является видом синхронной репликации, за которую нужно платить просадкой в производительности.
    - Т.к консенсус всегда требует голос большинства – если произойдёт сбой в сети и часть узлов будет отрезана от системы, то будет функционировать только та часть, в которой большинство узлов, остальные будут заблокированы.
    - Большинство алгоритмов подразумевают *определённое* кол-во узлов и не расчитаны на динамическое добавление узлов.
    - Системы консенсуса полагаются на таймаут для определения сбоев в узлах, поэтому в условиях нестабильной сети могут происходить нежелательные избыточные выборы ведущих узлов, что влияет на производительность системы и излишнее потребление ресурсов.
    - В условиях плохой сети, некоторые алгоритмы имеют неприятные корнер кейсы.

### Membership and Coordination Services
- Проекты как ZooKeeper или etcd преподносятся как "distributed key-value stores" и имеют api схожий с датабазой, но они не используются как дб.
- Они спроектированы чтобы держать небольшое кол-во данных, которое помещается полностью в память (запись на диск есть только для доп. гарантий). Эти данные реплицированы на все узлы в системе, используя total order broadcast алгоритм.
- ZooKeeper содержит не только TOB, но и другой функционал как:
    - *Линеаризированные атомарные операции*: например, с атомарной compare-and-set можно имплементировать распределённую блокировку (distributed lock).
    - Тотальный порядок операций
    - Обнаружение сбоев: долгоживущая сессия на ZooKeeper сервере, которая позволяет клиенту обмениваться heartbeat'ом для проверки доступности других узлов.
    - Уведомления об изменениях: клиенты могут получать нотификации об изменении настроек или о вводе нового клинета в кластер.
- модель ZooKeeper'а хорошо подходит для распределения нагрузки на узлы, примеры применения:
    - выбора ведущего узла в single-leader репликации
    - планировщики заданий
    - назначение и ребалансировка разделов датабазы на узлы, например, при добавлении нового узла в кластер.
- Обнаружение сервисов: обнаружение ip адресов сервисов, к которым мы хотим присоединиться
    - В облачных окружениях часто мы не знаем адрес сервиса наперёд, но когда он стартует, он регистрирует себя в регистре сервисов, где они могут быть потом обнаружены.
    - Обнаружение сервисов не требует строгого консенсуса, но обычно нам всё-равно необходим выбор ведущего узла и имеет смысл использовать эту информацию для того чтобы соощить всем сервисам какой узел является ведущим.
- Membership services
    - Эти сервисы определяет активные и живые узлы в кластере. В пред. главах мы видели что из-за потенциальных задержек в сети мы не можем гарантированно определить является ли узел живым или произошёл сбой. Но если мы используем обнаружение неисправностей в совокупности с консенсусом, то узлы могут придти к соглашению о том, какой узел считать активным а какой нет.

