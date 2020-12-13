#include "vector.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#define defaultAllocation 4;

void VectorNew(vector *v, int elemSize, VectorFreeFunction freeFn, int initialAllocation)
{
    assert(elemSize > 0);
    assert(initialAllocation >= 0);

    if (initialAllocation == 0) {
        initialAllocation = defaultAllocation;
    }

    v->freeFn = freeFn;
    v->elems = malloc(initialAllocation * elemSize);
    v->elemSize = elemSize;
    v->logLength = 0;
    v->allocLength = initialAllocation;
}

void VectorDispose(vector *v)
{
    if (v->freeFn != NULL) {
        for (int i = 0; i < v->logLength; ++i) {
            v->freeFn(VectorNth(v, i));
        }
    }

    free(v->elems);
}

int VectorLength(const vector *v)
{
    return v->logLength;
}

void *VectorNth(const vector *v, int position)
{
    assert(position >= 0);
    assert(position <= v->logLength);

    return (char *) v->elems + (position * v->elemSize);
}

void VectorReplace(vector *v, const void *elemAddr, int position)
{
    assert(position >= 0);
    assert(position <= v->logLength - 1);

    // Clear the previous element.
    void *currentElementAddr = VectorNth(v, position);
    if (v->freeFn != NULL) {
        v->freeFn(currentElementAddr);
    }

    // Insert a new element.
    memcpy(currentElementAddr, elemAddr, v->elemSize);
}

void VectorInsert(vector *v, const void *elemAddr, int position)
{
    assert(position >= 0);
    assert(position <= v->logLength);

    if (position == v->logLength) {
        // If we passed the position at the end of the array, we don't need to
        // move anything, just do append.
        return VectorAppend(v, elemAddr);
    }
    
    void *positionAddr = VectorNth(v, position);
    void *destAddr = VectorNth(v, position + 1);

    // First we move the contents of the array 1 element to the right. Starting
    // with `position`.
    memmove(destAddr, positionAddr, (v->logLength - position) * v->elemSize);

    // Second, we copy the data from `elemAddr` into `position`.
    memcpy(positionAddr, elemAddr, v->elemSize);

    v->logLength++;
}

void VectorAppend(vector *v, const void *elemAddr)
{
    if (v->logLength >= v->allocLength) {
        v->allocLength = v->allocLength * 2;
        v->elems = realloc(v->elems, v->allocLength * v->elemSize);
        assert(v->elems != NULL);
    }

    void *destAddr = VectorNth(v, v->logLength);
    memcpy(destAddr, elemAddr, v->elemSize);
    v->logLength += 1;
}

void VectorDelete(vector *v, int position)
{
    void *positionAddr = (void *) ((char *) v->elems + position * v->elemSize);
    void *nextAddr = (void *) ((char *) v->elems + (position + 1) * v->elemSize);

    if (v->freeFn != NULL) {
        v->freeFn(positionAddr);
    } else {
        // TODO: comment `free` out and see valgrind output.
        // Why we don't need free here?
        // free(positionAddr);
    }

    // Now shift the void pointers by 1 to the left.
    v->logLength--;
    memmove(positionAddr, nextAddr, (v->logLength - position) * v->elemSize);
}

void VectorSort(vector *v, VectorCompareFunction compare)
{
    qsort(v->elems, v->logLength, v->elemSize, compare);
}

void VectorMap(vector *v, VectorMapFunction mapFn, void *auxData)
{
    for (int i = 0; i < v->logLength; ++i) {
        mapFn(VectorNth(v, i), auxData);
    }
}

static const int kNotFound = -1;

int VectorSearch(
    const vector *v,
    const void *key,
    VectorCompareFunction searchFn,
    int startIndex,
    bool isSorted
)
{
    if (isSorted) {
        void *startAddr = VectorNth(v, startIndex);
        // TODO: We need a custom implementation of a binary search, bsearch
        // doesn't give us the index.
        void *elem = bsearch(key, startAddr, v->logLength, v->elemSize, searchFn);
        return elem == NULL ? kNotFound : 1;
    }

    for (int i = 0; i < v->logLength; ++i) {
        void* current = VectorNth(v, i);
        if (searchFn(key, current) == 0) {
            return i;
        }
    }

    return kNotFound;
} 
