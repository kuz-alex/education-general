#include "vector.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <stdbool.h> 

typedef struct {
    bool *bitmap;
    vector strings;
} group;

typedef struct {
    group *groups;
    int numGroups;
    int arrLen;
    int groupSize;
} sparsestringarray;

typedef void (*SSAMapFunction) (int index, const char *str, void *auxData);

void SSANew(sparsestringarray *ssa, int arrLen, int groupSize); 
bool SSAInsert(sparsestringarray *ssa, int index, const char *str); 
void SSAMap(sparsestringarray *ssa, SSAMapFunction mapfn, void *auxData); 
void SSADispose(sparsestringarray *ssa); 

int getVectorIndex(sparsestringarray *ssa, int groupIndex, int bitmapIndex)
{
    int vectorIndex = 0;
    for (int i = 0; i < bitmapIndex; ++i) {
        if (ssa->groups[groupIndex].bitmap[i] == true) {
            ++vectorIndex;
        }
    }

    return vectorIndex;
}

static void CountEmptyPrintNonEmpty(int index, const char *str, void *auxData)
{ 
    if (strcmp(str, "") != 0) {
        printf("Oooo! Nonempty string at index %d: \"%s\"\n", index, str);
    } else {
        (*(int *)auxData)++;
    }
}
 
void StringFree(void *elem)
{
    free(*(char **) elem);
}

void SSANew(sparsestringarray *ssa, int arrLen, int groupSize)
{
    assert(arrLen > 0);
    assert(groupSize > 0);

    ssa->arrLen = arrLen;
    ssa->groupSize = groupSize;
    ssa->numGroups = ssa->arrLen / ssa->groupSize;

    // Allocate memory for groups.
    ssa->groups = malloc(sizeof(group) * ssa->numGroups);

    // Insert empty groups into the allocated memory.
    for (int i = 0; i < ssa->numGroups; ++i) {
        group newGroup;
        // Each group contains `groupSize` number of booleans char pointers.
        newGroup.bitmap = malloc(ssa->groupSize * sizeof(bool));
        VectorNew(&newGroup.strings, sizeof(char *), StringFree, 4);
        
        for (int j = 0; j < ssa->groupSize; ++j) {
            newGroup.bitmap[j] = false;
        }
        ssa->groups[i] = newGroup;
    }
}

bool SSAInsert(sparsestringarray *ssa, int index, const char *str)
{
    int groupIndex = index / ssa->groupSize;
    int bitmapIndex = index % ssa->groupSize;
    int vectorIndex = getVectorIndex(ssa, groupIndex, bitmapIndex);

    // Important to use it as a pointer, so we don't update strings in a scope
    // allocated memory.
    group *current = &ssa->groups[groupIndex];
    char *strCopy = strdup(str);

    if (current->bitmap[bitmapIndex] == true) {
        VectorReplace(&current->strings, &strCopy, vectorIndex);
        return false;
    }

    VectorInsert(&current->strings, &strCopy, vectorIndex);
    current->bitmap[bitmapIndex] = true;
    return true;
}

void SSADispose(sparsestringarray *ssa)
{
    for (int i = 0; i < ssa->numGroups; ++i) {
        VectorDispose(&ssa->groups[i].strings);
        free(ssa->groups[i].bitmap);
    }

    free(ssa->groups);
}

char *kEmptyString = "";
void SSAMap(sparsestringarray *ssa, SSAMapFunction mapfn, void *auxData)
{
    for (int i = 0; i < ssa->numGroups; ++i) {
        for (int j = 0; j < ssa->groupSize; ++j) {

            if (ssa->groups[i].bitmap[j] == true) {
                int vectorIndex = getVectorIndex(ssa, i, j);
                char *str = *(char **) VectorNth(&ssa->groups[i].strings, vectorIndex);
                mapfn(i + j, str, auxData);
            } else {
                mapfn(i + j, kEmptyString, auxData);
            }
        }
    }
}

int main(int argc, char **argv) 
{ 
    sparsestringarray ssa;

    SSANew(&ssa, 70000, 35);

    SSAInsert(&ssa, 33001, "need");
    SSAInsert(&ssa, 58291, "more");
    SSAInsert(&ssa, 33000, "Eye");
    SSAInsert(&ssa, 33000, "I");
    SSAInsert(&ssa, 67899, "cowbell");

    int numEmptyStrings = 0;
    SSAMap(&ssa, CountEmptyPrintNonEmpty, &numEmptyStrings);
    printf("%d of the strings were empty strings.\n", numEmptyStrings);

    SSADispose(&ssa);
    return 0;
}

