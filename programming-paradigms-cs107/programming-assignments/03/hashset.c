#include "hashset.h"
#include <assert.h>
#include <stdlib.h>
#include <string.h>

void HashSetNew(hashset *h, int elemSize, int numBuckets, HashSetHashFunction
        hashfn, HashSetCompareFunction comparefn, HashSetFreeFunction freefn
)
{
    assert(elemSize > 0);
    assert(numBuckets > 0);
    assert(hashfn != NULL);
    assert(comparefn != NULL);

    h->elemSize = elemSize;
    h->numBuckets = numBuckets;
    h->hashfn = hashfn;
    h->comparefn = comparefn;
    h->freefn = freefn;

    h->buckets = malloc(numBuckets * sizeof(vector));
    for (int i = 0; i < numBuckets; ++i) {
        vector v;
        VectorNew(&v, elemSize, freefn, 4);
        h->buckets[i] = v;
    }
}

void HashSetDispose(hashset *h)
{
    for (int i = 0; i < h->numBuckets; ++i) {
        //if (h->freefn != NULL) {}
        VectorDispose(&h->buckets[i]);
    }

    free(h->buckets);
}

int HashSetCount(const hashset *h)
{
    int result = 0;
    for (int i = 0; i < h->numBuckets; ++i) {
        vector *current = &h->buckets[i];
        result += VectorLength(current);
    }

    return result;
}

void HashSetMap(hashset *h, HashSetMapFunction mapfn, void *auxData)
{
    for (int i = 0; i < h->numBuckets; ++i) {
        vector *current = &h->buckets[i];
        VectorMap(current, mapfn, auxData);
    }
}

void HashSetEnter(hashset *h, const void *elemAddr)
{
    int key = h->hashfn(elemAddr, h->numBuckets);
    // TODO: Note down how if we're not using the pointer here, C implicitly
    // copies the vector from the list of vectors and we're operating on a
    // local copy.
    vector *targetBucket = &h->buckets[key];
    int foundIndex = VectorSearch(targetBucket, elemAddr, h->comparefn, 0, false);

    if (foundIndex != -1) {
        VectorReplace(targetBucket, elemAddr, foundIndex);
    } else {
        VectorAppend(targetBucket, elemAddr);
    }
}

void *HashSetLookup(const hashset *h, const void *elemAddr)
{
    int key = h->hashfn(elemAddr, h->numBuckets);
    vector *targetBucket = &h->buckets[key];
    int result = VectorSearch(targetBucket, elemAddr, h->comparefn, 0, false);

    return result == -1 ? NULL : VectorNth(targetBucket, result);
}
