
# Chapter 3. Storage and Retrieval

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
  - Perform compaction and optionally merging of the segments
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


### Comparing B-Trees and LSM-Trees
### Other Indexing Structures

## Transaction Processing or Analytics?

## Column-Oriented Storage

# Chapter 4. Encoding and Evolution
