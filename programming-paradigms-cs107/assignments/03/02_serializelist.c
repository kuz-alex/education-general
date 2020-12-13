#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void *serializeList(const void *root)
{
    void *serializedList = malloc(sizeof(int));
    int serializedMemorySize = sizeof(int);
    int newStrLen;
    int len = 0;

    void **current = &root;

    while(*current != NULL) {
        // Dereference a pointer to get to the next node.
        current = *current;
        char *s = (char *) current + sizeof(void *);

        // Allocate memory for the new string.
        newStrLen = strlen(s) + 1;
        serializedMemorySize += newStrLen;
        serializedList = realloc(serializedList, serializedMemorySize);

        // Insert a new string starting from the location where the previous string ends.
        void *newStringStart =
            (char *) serializedList + (serializedMemorySize - newStrLen);
        strcpy(newStringStart, s);

        ++len;
    } 

    *(int *) serializedList = len;
    return serializedList;
}

int main()
{
    // Construct the linked list.
    char *strs[3] = {"Red", "Yellow", "Pink"};

    void *rootNode = malloc(sizeof(void *));

    void **prevPtr = &rootNode;
    void *newNode;
    char *strStart;

    printf("Initial pointer is: %p\n", *prevPtr);

    for (int i = 0; i < 3; ++i) {
        newNode = malloc(sizeof(void *) + strlen(strs[i]) + 1);

        // Copy the string into the space that follows `nextNode` link.
        strStart = (char *) newNode + sizeof(void *);
        strcpy(strStart, strs[i]);

        printf("Setting a new pointer to the pointer %p, its: %p.\n", *prevPtr, newNode);
        // Point the pointer of the previous node to the current node (this doesn't change prevPtr).
        *prevPtr = newNode;
        // Copy the address into a `prevPtr` variable.
        prevPtr = newNode;
    }

    // Iterate through that list.
    void **curr = &rootNode;
    while (*curr != NULL) {
        // We go to the next one right away.
        curr = *curr;
        char *str = (char *) curr + sizeof(void *);
        printf("Iterating: \"%s\".\n", str);
    }

    printf("Serializing the list and printing it out: \"");
    void *serialized = serializeList(rootNode);
    char *str = (char *) serialized + sizeof(int);
    for (int i = 0; i < *(int *) serialized; ++i) {
        printf("%s ", str);
        str = str + strlen(str) + 1;
    }
    printf("\"\n");

    return 0;
}

