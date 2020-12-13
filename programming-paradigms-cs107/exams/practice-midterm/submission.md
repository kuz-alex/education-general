# Practice midterm.

## Problem 1.
_line 1: azie.bacar[azie.ame[2]] += catch->farallon.aqua[4]_

<!-- Load `azie.ame[2]` in R1 -->
R1 = .2 M[SP + 8]

SP = SP - 24        ; Space for locals.

R2 = M[SP]          ; load address of `catch`.
R2 = M[R2 + 24]     ; load `catch->farallon.aqua[4]` (+ 8 + 4 * 4).

R3 = SP + 52        ; load `&azie.bacar[0]` char pointer (+ 28 + 24).
// Offset into `azie.bacar` array is 1, it's a char array. Thus, we just use value of R1 multiplied by 1.
R3 = R3 + R1        ; calculate an address of `azie.bacar[azie.ame[2]]`.

R4 = M[R3]          ;
R5 = R4 + R2        ; compute the new value.
M[R3] = R5          ; flush new value back.


_line 2: ((appetizer *) (((dessert *) &oola.quince)->farallon.garydanko))->quince = 0;_

R1 = SP + 20        ; load address of `oola.quince` and pretend it's `dessert *`.
R1 = M[R1 + 4]      ; load `->farallon.garydanko` and pretend it's `appetizer *`.
M[R1 + 16] = 0      ; store 0 in `->quince`.


_line 3: return (*dinnerisserved((short *) &indigo, &oola)).farallon.aqua;_
R1 = SP + 60        ; load address of `indigo` (+ 36 + 24).
R2 = SP + 4         ; load address of `oola`.

SP = SP - 8         ; create space for parameters.
M[SP] = R1          ; initialize 1st parameter.
M[SP + 4] = R2      ; initialize 2nd parameter.

CALL <dinnerisserved> ;

R3 = M[RV]          ; dereference `dessert *`.
R3 = M[R3 + 8]      ; load `farallon.aqua` (+ 4 + 4).
RV = R3             ; store.

SP = SP + 8         ; clean up parameters.
SP = SP + 24        ; clean up locals.
RET                 ;

## Problem 2.

```
void *packetize(const void *image, int size, int packetSize)
{
    // Determine how many full nodes are there (truncating the remainder).
    int fullNodesN = size / packetSize;

    if (fullNodesN == 0) {
        // Handle the case when there's no full sized nodes.
    }

    void *rootLink = NULL;
    void **currp = &rootLink;

    for (int i = 0; i < fullNodesN; ++i) {
        char *packetStart = (char *) image + (i * packetSize);

        // Create a new node in the linked list.
        void *newNode = malloc(packetSize + sizeof(void *));
        void *nextNodeLink = (char *) newNode + packetSize;

        memcpy(newNode, packetStart, packetSize);
        nextNodeLink = NULL;

        // Copy a pointer of the current node into a previous one.
        *currp = newNode;
        currp = &nextNodeLink;
    }

    int remainderSize = size % packetSize;
    if (remainderSize > 0) {
        // Create a last node and insert it in the list.
        void *newNode = malloc(remainderSize + sizeof(void *));
        void *nextNodeLink = (char *) newNode + remainderSize; 
        void *lastNodePos = (char *) (image + (size - remainderSize));

        memcpy(newNode, lastNodePos, remainderSize);
        nextNodeLink = NULL;
        // Insert a new node into the linked list.
        *currp = newNode;
    }

    return *rootLink;
}
```

## Problem 3.
```
typdef int (*MultiSetHashFunction) (const void *elem, int numBuckets);
typdef int (*MultiSetCompareFunction) (const void *elem, const void *elem2);
typdef void (*MultiSetMapFunction)(void *elem, int count, void *auxData);
typdef void (*MultiSetFreeFunction)(void *elem);

typedef struct {
    hashset elements;
    int elemSize;
    MultiSetFreeFunction free;
} multiset;

void MultiSetNew(multiset *ms, int elemSize, int numBuckets,
        MultiSetHashFunction hash, MultiSetCompareFunction compare,
        MultiSetFreeFunction free)
{
    hashset h;
    HashSetNew(&h, elemSize, numBuckets, hash, compare, free);

    ms->elements = h;
    ms->elemSize = elemSize;
    ms->free = free;
}

void MultiSetDispose(multiset *ms)
{
    HashSetDispose(&ms->elements);
}

/*
   Important note, we're storing `client elem + multiplicity int` elements in
   the hashset, the client won't know about this, since his element goes first
   and he can always cast to the proper type. 
*/
void MultiSetEnter(multiset *ms, const void *elem)
{
    // Whatever is inside `hashset` will be passed to a compare function.
    void *match = HashSetLookup(&ms->elements, elem);

    if (match != NULL) {
        memcpy(match, elem, ms->elemSize);
        int *multiplicityPos = (int *) ((char *) match + ms->elemSize);
        *multiplicityPos += 1
    } else {
        // New element insertion.
        void *newElem = malloc(ms->elemSize + sizeof(int));
        memcpy(newElem, elem, ms->elemSize);
        int *multiplicityPos = (int *) ((char *) newElem + ms->elemSize);
        *multiplicityPos = 1
    }

}

typedef void (*MultiSetMapFunction)(void *elem, int count, void *auxData);
void MultiSetMap(multiset *ms, MultiSetMapFunction map, void *auxData)
{
    void multiMap(void *elemAddr, void *auxData) {
        int elemN = *(int *) ((char *) elemAddr + ms->elemSize);
        map(elemAddr, elemN, auxData);
    }

    HashSetMap(&ms->elements, multiMap, auxData);
}

```

# Problem 04.

```
typedef struct {
    const char *licensePlate;
    int numTickets;
} maxTicketsStruct;

void FindQueenOfParkingInfractions(multiset *ms, char licensePlateOfQueen[])
{
    maxTicketsStruct *maxTicketsElem;
    int biggestN = 0;

    void map(void *elem, int count, void *auxData) {
        if (count > maxTicketsElem->numTickets) {
            maxTicketsElem->numTickets = count;
            strcpy(maxTicketsElem->licensePlate, elem);
        }
    }

    MultiSetMap(ms, map, 0);

    strcpy(licensePlateOfQueen, maxTicketsElem->licensePlate);
}
```

# Problem 05.
a. Because size of each address is 4 bytes, our instruction in machine level
code is just a binary sequence, if we use 3 addresses in a single instruction,
then it should be long enough to contain all of them. Our instructions should
be more than 4 bytes long though, so we can specify a type of instruction, a
4-bytes address (pointer) and a "load" operation.

b.
    Saved PC at the top:
        Becuase of the functions with (`...`) in parameters, for those we're
        creating an arbitrary number of parameters and we will have harder time
        figure out where our SP at. Technically we can still figure it out from
        the map (like in "printf" function), but it's harder.
    Saved PC at the bottom:
        Because we don't know the number of local variables before calling a
        function. If we were to store it at the bottom, we would need to drag
        it down manually with every new local variable.
c.
Truncation works with an assumption that our assembly code will be copying
bytes "as is", without additional logic like copying the least significant byte
for "char".

```
int isLittleEndian()
{
    // put the number which binary pattern is `0000 0001 0000 0000` in little-endian.
    short sh = 256;
    // char is 1 byte (8 bits).

    // In little-endian info about that 1 bit in the second byte is lost. In
    // big-endian, original pattern is: "0000 0000 1000 0000", and if we try to
    // copy the right part, we will end up with  "1000 0000".

    char i = (char) sh;
    return (i > 0) ? 0 : 1;
}
```
