## Problem 1.

line 1:
    1. Forgot to cast char values with `.1`. Be more aware of the type when
       trying to load or store ANY value.
line 2:
Correct.

line 3:
    Incorrect handling of return value. Pointer arithmetic aws wrong.
    We dereference a pointer and go 8 bytes to the right, which is equals to
    doing pointer arithmetic, RV + 8, and then, since `aqua` is a pointer, we
    don't dereference it, but simply pass an address. `RV = RV + 8.


## Problem 2.
Correct overall, but much less elegant than in the solution.

## Problem 3.

MultiSetNew:
    Don't need to create the hashset for the multiset. A client is supposed to
    send it with the `ms` struct.

MultiSetDispose:
    Correct.

MultiSetEnter:
    It's better to set values without `memcpy`:
    ```
    void *newElem = malloc(ms->elemSize + sizeof(int));
    memcpy(newElem, elem, ms->elemSize);

    // Better
    char pair[ms->elemSize + sizeof(int)];
    *(int *)(pair + ms->elemSize) = 1;
    ```
    We're doing pointer arithmetic and type-casting in the left side.

MultiSetMap:
Correct, but they moved the function outside the scope and constructed an aux
object with some original data to pass there.

## Problem 4.
Idea is correct, but there are mistakes.
I created a pointer to struct on the stack, while I should've created the whole
struct there.
```
    maxTicketsStruct *maxTicketsElem; // in that case I would need to use malloc
    // instead create it on the stack.
    maxTicketsStruct maxTicketsElem;

    // ...

    strcpy(licensePlateOfQueen, maxTicketsElem.licensePlate);
```

And I also needed to move the function outside the scope and just pass
`maxTicketsInfo` with `auxData`.
```
void IdentifyContender(void *elem, int count, void *auxData)
{
    maxTicketsElem *max = (maxTicketsStruct *) auxData;
    if (count > max->numTickets) {
        max->numTickets = count;
        strcpy(max->licensePlate, elem);
    }
}

void FindQueenOfParkingInfractions(multiset *ms, char licensePlateOfQueen[])
{
    maxTicketsStruct maxTicketsElem;
    int biggestN = 0;

    MultiSetMap(ms, map, &maxTicketsElem);

    strcpy(licensePlateOfQueen, maxTicketsElem.licensePlate);
}
```

## Problem 5.
Got two of the questions correctly, "c" is very vague.

