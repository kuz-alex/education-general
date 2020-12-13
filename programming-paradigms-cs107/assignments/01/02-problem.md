# "Problem 2: Scheme" submission.
```
typedef enum {
    Integer, String, List, Nil
} nodeType;


char *ConcatAll(nodeType *list) {
    if (*list == nodeType.Nil || *list == nodeType.Integer) {
        return strdup("");
    } else if (*list == nodeType.String) {
        char *strLocation = (char *) list + sizeof(nodeType);
        return strdup(strLocation);
    } else {
        // Handle `List` type.

        nodeType *nextLocation = (list + 1);
        char *firstString = ConcatAll(nextLocation);
        nextLocation = (list + 1);
        char *secondString = ConcatAll(nextLocation);

        return strcat(s1, s2); // Mistake.
    }
}
```

# "Problem 2: Scheme" official solution.
```
char *ConcatStrings(char *s1, char *s2) {
    char *result = malloc(strlen(s1) + strlen(s2) + 1);
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

char *ConcatAll(nodeType *list) {
    switch (*list) {
        case Integer:
        case Nil:
            return strdup("");
        case String:
            return strdup((char *) (list + 1));
    }

    nodeType **nextLocs = (nodeType **) (list + 1);
    char *s1 = ConcatAll(nextLocs[0]);
    char *s2 = ConcatAll(nextLocs[1]);

    return ConcatStrings(s1, s2);
}
```

