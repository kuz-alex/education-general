#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "assert.h"
#include <stdbool.h> 

const int kInitialCapacity = 16;

typedef int (*CompareFn)(void *elem1, void *elem2);

typedef struct {
    void *elems;
    int nodeSize;
    int elemSize;
    int logicLength;
    int allocLength;
    CompareFn cmpfn;
} sortedset;

typedef struct {
    int left;
    int right;
} offsets;

void *SetNth(sortedset *s, int position);

bool insertNodeInOrder(sortedset *set, void *newElem, int *currOffset);

void SetNew(sortedset *stringSet, int elemSize, CompareFn cmpfn)
{
    assert(elemSize > 0);
    assert(cmpfn != NULL);

    stringSet->elemSize = elemSize;
    stringSet->nodeSize = stringSet->elemSize + sizeof(offsets);

    stringSet->cmpfn = cmpfn;
    stringSet->logicLength = 0;
    stringSet->allocLength = kInitialCapacity;

    stringSet->elems = malloc(sizeof(int) + stringSet->nodeSize * stringSet->allocLength);

    int initialRootNodeValue = -1;
    memcpy(stringSet->elems, &initialRootNodeValue, sizeof(int));
}

offsets *SetNodeOffsets(sortedset *s, void *elemPtr)
{
    char *offsetsStart = (char *) elemPtr + s->elemSize;
    return (offsets *) offsetsStart;
}

bool SetAddNode(sortedset *set, void *elemPtr)
{
    if (*(int *) set->elems == -1) {
        int zeroIndex = 0;
        memcpy((char *) set->elems, &zeroIndex, sizeof(int));
        // Insert a root node into an empty set.
        void *start = SetNth(set, 0);
        memcpy(start, elemPtr, set->elemSize);
        int *newOffsets = (int *) ((char *) start + set->elemSize);
        newOffsets[0] = -1;
        newOffsets[1] = -1;
    } else {
        // When the binary tree is not empty, we want to insert the next node
        // based on the value in the previous one.
        void *baseNode = SetNth(set, 0);
        offsets *baseOffsets = (offsets *) ((char *) baseNode + set->elemSize);

        int *currentOffset = set->cmpfn(elemPtr, baseNode) > 0
            ? &baseOffsets->right
            : &baseOffsets->left;

        insertNodeInOrder(set, elemPtr, currentOffset);
    }

    set->logicLength += 1;
    return true;
}

void *SetNth(sortedset *s, int position)
{
    void *nodesStart = (char *) s->elems + sizeof(int);
    return (char *) nodesStart + (s->nodeSize * position);
}

bool insertNodeInOrder(sortedset *set, void *newElem, int *currOffset)
{
    if (*currOffset == -1) {
        // Insert the node
        int newNodeOffset = set->logicLength;
        void *newAddress = SetNth(set, newNodeOffset);
        // Update the int value in the previous node `right` of `left` part of the struct.
        *currOffset = newNodeOffset;
        memcpy(newAddress, newElem, set->elemSize);
        int *newOffsets = (int *) ((char *) newAddress + set->elemSize);
        newOffsets[0] = -1;
        newOffsets[1] = -1;

        return true;
    }

    void *currElem = SetNth(set, *currOffset); 
    offsets *currOffsets = (offsets *) ((char *) currElem + set->elemSize);

    if (set->cmpfn(newElem, currElem) > 0) {
        // Recurse into right node.
        return insertNodeInOrder(set, newElem, &currOffsets->right);
    } else if (set->cmpfn(newElem, currElem) < 0) {
        // Recurse into left node.
        return insertNodeInOrder(set, newElem, &currOffsets->left);
    } else {
        return false;
    }
}

void *SetSearch(sortedset *set, void *needlePtr)
{
    // We store the offset of the first element in the beginning of the memory.
    int currOffset = *(int *) set->elems;

    while (currOffset != -1) {
        void *currElem = SetNth(set, currOffset);
        offsets *currOffsets = (offsets *) ((char *) currElem + sizeof(set->elemSize));

        printf("Going through the element: %d (%d, %d).\n", *(int *) currElem, currOffsets->left, currOffsets->right);

        if (set->cmpfn(needlePtr, currElem) == 0) {
            // Found the element.
            return currElem;
        }  else if (set->cmpfn(needlePtr, currElem) > 0) {
            currOffset = currOffsets->right;
        } else {
            currOffset = currOffsets->left;
        }
    }

    return NULL;
}


int strCompare(void *elem1, void *elem2)
{
    char *s1 = *(char **) elem1;
    char *s2 = *(char **) elem2;
    return strcmp(s1, s2);
}

int numCompare(void *elem1, void *elem2)
{
    int n1 = *(int *) elem1;
    int n2 = *(int *) elem2;
    return n1 - n2;
}

int main()
{
    char *str = strdup("Mo");
    char *str2 = strdup("Yu");

    sortedset numsTree;
    SetNew(&numsTree, sizeof(int), numCompare);
    int nums[7] = {58, 63, 712, 1111, 5551, 18, 35};
    SetAddNode(&numsTree, &nums[0]);
    SetAddNode(&numsTree, &nums[1]);
    SetAddNode(&numsTree, &nums[2]);
    SetAddNode(&numsTree, &nums[3]);
    SetAddNode(&numsTree, &nums[4]);
    SetAddNode(&numsTree, &nums[5]);
    SetAddNode(&numsTree, &nums[6]);

    void* found = SetSearch(&numsTree, &nums[2]);
    offsets foundOffsets = *(offsets *) ((char *) found + sizeof(int));
    printf("Found the node: %d (%d, %d).\n", *(int *) found, foundOffsets.left, foundOffsets.right);

    sortedset tree;
    SetNew(&tree, sizeof(char *), strCompare);
    SetAddNode(&tree, &str);
    SetAddNode(&tree, &str2);

    free(str);
    free(str2);

    return 0;
}
