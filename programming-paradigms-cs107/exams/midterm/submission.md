# Midterm exae, today's date: 16-07-20

# Problem 1
```
AR: 

76 [spinach                       ] veggie *
72 [carrot.parsnip[2]             ] fruit *
68 [carrot.parsnip[1]             ] fruit *
64 [carrot.parsnip[0]             ] fruit *
60 [carrot.potato.cherry[12..15]  ] char
56 [carrot.potato.cherry[8..11]   ] char
52 [carrot.potato.cherry[4..7]    ] char
48 [carrot.potato.cherry[0..3]    ] char
44 [carrot.potato.banana          ] char *
40 [carrot.potato.apple           ] int
36 [carrot.pea[4..5]              ] short
32 [carrot.pea[2..3]              ] short
28 [carrot.pea[0..1]              ] short
24 [Saved PC                      ] address
20 [date.cherry[12..15]           ] char
16 [date.cherry[8..11]            ] char
12 [date.cherry[4..7]             ] char
08 [date.cherry[0..3]             ] char
04 [date.banana                   ] char *
00 [date.apple                    ] int

fruit *casserole(veggie carrot, veggie *spinach)
{
    [0]: fruit date;
    SP = SP - 24; allocate space for `fruit date`;

    [1]: `date.cherry[4] = spinach->pea[carrot.potato.apple];`

    R1 = M[SP + 40] ; load `carrot.potato.apple`.
    R1 = R1 * 2     ; calculate offset into `spinach->pea` short array.
    R2 = M[SP + 76] ; load address of `spinach` struct.
    R2 = R2 + 0     ; advance address to `spinach->pea`.
    R2 = R2 + R1    ; advance address to `spinach->pea[R1]`.
    R2 = .2 M[R2]   ; load value from `spinach->pea[R1]`.

    R3 = SP + 12    ; calculate address of `date.cherry[4]`.
    M[R3] = .4 R2   ; flush the value there.

    [2]: ((veggie *) (((veggie *) carrot.parsnip[0])->parsnip))->potato.banana =
                                                                *(char **) &date;

    R1 = M[SP + 64] ; load address in `carrot.parsnip[0]` and pretend it's `veggie *`.
    R1 = R1 + 36    ; advance address to `->parsnip` and pretend it's `veggie *`.
    R1 = R1 + 16    ; advance address to `->potato.banana`.
    R2 = SP         ; load address of `date` and pretend it's `char *`.
    M[R1] = .4 R2   ; store address of `date`.

    [3]: return tort(&(spinach->parsnip[2]), date.banana[4]) + 10;

    R1 = M[SP + 76]   ; load `spinach *`.
    R1 = R1 + 44      ; advance address to `spinach->parsnip[2]`.

    R2 = M[SP + 4]    ; load `date.banana *`.
    R2 = .1 M[R2 + 4] ; advance address and load `date.banana[4]`.

    // Allocate space for function parameters (char * and a char). Note that
    // we still allocate 4-bytes for char, to optimize memory layout for hw.
    SP = SP - 8       ; 
    M[SP] = R1        ;
    M[SP + 4] = .1 R2 ;
    CALL <tort>       ;
    
    R3 = 10 * 24; calculate offset into `fruit` array.
    RV = RV + R3; do pointer arithmetic `tort() + 10`.

    SP = SP + 8; clean callee function parameters.
    SP = SP + 24; cleanup local variables
    RET;
}

```

# Problem 3.

```
typedef struct {
    char *name;
    char **friends;
    int numfriends;
} person;

person *decompress(void *image)
{
    int imageLen = *(int *) image;

    void *currentAddress = (char *) image + sizeof(int);
    // Allocate space for n of `person *`
    person *friends = malloc(imageLen * sizeof(person *));

    for (int i = 0; i < imageLen; ++i) {
        friends[i] = malloc(sizeof(person));
        // Get the number of bytes for a string with '\0' character.
        int strLen = strlen(currentAddress) + 1;

        // Dynamically allocate the string and store a pointer to it.
        friends[i]->name = strdup(currentAddress);

        // If there's any offset, advance a pointer to that number of bytes.
        int extraOffset = strLen % 4;
        if (extraOffset != 0) {
            currentAddress = (char *) currentAddress + extraOffset;
        }

        // Get the number of friends.
        int friendsNumber = *(int *) currentAddress;
        // Allocate space for friends `char *`.
        friends[i]->friends = malloc(friendsNumber * sizeof(char *));

        // Advance current address further.
        char *friendPtr = (char *) currentAddress + sizeof(int);

        for (int j = 0; j < friendsNumber; ++j) {
            // Dynamically allocate the string.
            char *friendName = strdup((char *) friendPtr);

            // Now copy the `char *` pointer into our array.
            friends[i]->friends[j] = friendName;

            friendPtr++; // advance one `char *` further.
        }
    }

    return friends;
}
```

# Problem 2.
```
typedef struct {
    void *elems;
    int elemsize;
    int count;
    int alloclength;
    HashSetHashFunction hashfn;
    HashSetFreeFunction freefh;
    HashSetCompareFunction cmpfn;
} hashset;

HashSetNew(hashset *hs, int elemsize, HashSetHashFunction hashfn,
        HashSetCompareFunction cmpfn, HashSetFreeFunction freefn)
{
    hs->elems = malloc((sizeof(bool) + elemsize) * 64);
    hs->alloclength = 64;
    hs->count = 0;

    hs->elemsize = elemsize;
    hs->hashfn = hashfn;
    hs->cmpfn = cmpfn;
    hs->freefn = freefn;
}

bool HashSetEnter(hashset *hs, void *elem)
{
    if (hs->count < ((3 / 4) * hs->alloclength))
        HashSetRehash(hs);

    // First check if the current element is already inserted.
    int newElementIndex = hs->hashfn(elem, hs->alloclength);
    int bucket = (char *) hs->elems + (hs->elemsize + sizeof(bool)) * newElementIndex;
    void *bucketElement = (char *) bucket + sizeof(bool)

    if (*(bool *) bucket == 0) {
        // If the bucket is empty, insert a new element.
        *(bool *) bucket = 1;
        memcpy(bucketElement, elem, hs->elemsize);
        return 1;
    }

    if (hs->cmpfn(elem, bucketElement) != 0) {
        // If the new element is different from the one in the current bucket,
        // find a new position.
        for (int j = 1; *(int *) bucket != 0; ++j) {
            // Get a new bucket index.
            newElementIndex = (newElementIndex + j) % hs->alloclength;
            // Update the current bucket.
            bucket = (char *) hs->elems + (hs->elemsize + sizeof(bool)) * newElementIndex;
        }

        // Made it here, this means we found an empty bucket.
        *(int *) bucket = 1;
        bucketElement = (char *) bucket + sizeof(bool);
    }

    memcpy(bucketElement, elem, hs->elemsize);
    return 0;
}


bool HashSetRehash(hashset *hs)
{
    hs->alloclength = hs->alloclength * 2;
    void *oldElems = hs->elems;
    hs->elems = malloc((sizeof(bool) + elemsize) * hs->alloclength);

    // Iterate over previous elements.

    for (int i = 0; i < alloclength; ++i) {
        void *bucket = (char *) oldElems + (hs->elemsize + sizeof(bool)) * i;

        if (*(int *) bucket == 1) {
            HashSetEnter(hs, elem);
        }
    }
}
```
