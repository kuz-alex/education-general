#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "vector.h"
#include "hashset.h"

typedef struct {
    hashset mappings;
    int keySize;
    int valueSize;
} multitable;


/**
 * Function: MultiTableNew
 */
typedef int (*MultiTableHashFunction)(const void *keyAddr, int numBuckets);
typedef int (*MultiTableCompareFunction)(const void *keyAddr1, const void *keyAddr2);

void MultiTableNew(multitable *mt, int keySizeInBytes, int valueSizeInBytes,
        int numBuckets, MultiTableHashFunction hash, MultiTableCompareFunction compare)
{
    int elemSize = keySizeInBytes + sizeof(vector);

    hashset h;
    HashSetNew(&h, elemSize, numBuckets, hash, compare, NULL);

    mt->keySize = keySizeInBytes;
    mt->valueSize = valueSizeInBytes;
    mt->mappings = h;
};

/**
 * Function: MultiTableEnter
 */
void MultiTableEnter(multitable *mt, const void *keyAddr, const void *valueAddr)
{
    void *elem = HashSetLookup(&mt->mappings, keyAddr);
    vector *vectorStart = (vector *) ((char *) elem + mt->keySize);

    if (elem == NULL) {
        // Create a new entry `key + vector` in the hashset.
        int elemSize = mt->keySize + sizeof(vector);
        elem = malloc(elemSize);
        memcpy(elem, keyAddr, mt->keySize);

        VectorNew(vectorStart, mt->valueSize, NULL, 1);
        VectorAppend(vectorStart, valueAddr);

        HashSetEnter(&mt->mappings, elem);
    }

    VectorAppend(vectorStart, valueAddr);
};

/**
 * Function: MultiTableMap
 */
typedef int *(MultiTableMapFunction)(void *keyAddr, void *valueAddr, void *auxData);

void MultiTableMap(multitable *mt, MultiTableMapFunction map, void *auxData)
{
    void hashMap(void *keyAddr, void *auxData) {
        vector *vecStart = (vector *) ((char *) keyAddr + mt->keySize);
        int vecLen = VectorLength(vecStart);

        for (int i = 0; i < vecLen; ++i) {
            map(keyAddr, VectorNth(vecStart, i), auxData);
        }
    }

    HashSetMap(&mt->mappings, hashMap, auxData);
};

