# Problem 1.

```
sizeof(grace) == 16

/* Line: grace[2].clementina[12] = *bernice; */

R1 = M[SP + 8]      ; load bernice pointer
R1 = .2 M[R1]       ; load bernice

R2 = M[SP + 4]      ; load grace pointer
// [0] MISTAKE: We don't dererference it here before calculating the final
// address. A very key moment is that we dereference only a single value, the
// end value after all the pointer arithmetic.
R2 = M[R2 + 48]     ; load `grace + 2 * sizeof(grace)`

M[R2 + 12] = .1 R1  ; store bernice in `R2.clementina[12]`

/* Line: ((alley *) (grace->minna))->jessie[2].clara += 960; */

R1 = M[SP + 4]      ; load `grace` pointer
R1 = M[R1 + 4]      ; load `grace->minna` pointer

// [1] MISTAKE: We didn't dereference a jessie pointer, we need to dereference
// it before doing pointer arithmetic with the address.
R2 = M[R1 + 84]     ; pretend and load `->jessie[2].clara` (+ 16 + (24 * 2) + 20).
R3 = R2 + 960       ;
// [1] MISTAKE: Since we're storing a char, we should cast it with `.1`.
M[R1 + 84] = R3     ; store


/* Line: return *(char **) washburn(grace + 2, &bernice[2]); */
SP = SP - 8         ; make space for parameters

R1 = M[SP + 4]      ; load `grace` pointer.
R2 = R1 + 48        ; calculate `grace + 2 * sizeof(alley)` pointer.
M[SP] = R2          ; store function parameter, `grace + 2`.

R3 = M[SP + 8]      ; load `bernice` pointer.
R4 = R3 + 4         ; calculate `bernice[2]` pointer (bernice + 2 * sizeof(short)).
M[SP + 4] = R4      ; store function parameter, address of `bernice[2]`.

CALL <washburn>     ;

SP = SP + 8         ; clean up
RV = M[RV]          ; dereference a return value and return it.
RET                 ;
```

# Problem 2.

```
typedef struct {
    char *girl;
    char *boy;
} couple;

void CoupleFree(void *elem)
{
    couple c = (couple *) elem;
    free(c->boy);
    free(c->girl);
}

vector generateAllCouples(vector *boys, vector *girls)
{
    vector couples;
    VectorNew(&couples, sizeof(couple), CoupleFree, 0);

    for (int i = 0; i < VectorLength(boys); ++i) {
        char *boyName = *(char **) VectorNth(boys, i);

        // Create a new couple.
        couple c;

        for (int j = 0; j < VectorLength(girls); ++j) {
            if (i == j) {
                // Don't include "dot" product.
                continue;
            }

            char *girlName = *(char **) VectorNth(girls, j);

            c.boy = strdup(boyName);
            c.girl = strdup(girlName);

            // MISTAKE, we cant't pass a `couple` by value here, since Append
            // takes a void pointer. We need to pass a pointer to it with `&c`.
            VectorAppend(&couples, c);
        }
    }

    return couples;
}
```

# Problem 3.

Pseudo:
1. At the start of a node, get the size of the first packet.
    Allocate memory for the packet.
    Copy the packet.

2. Go to the next packet.
    Reassign a pointer, move it by `packet size` bytes to the right. We
    should have a pointer to `short`.

3. Check if it's the end of a node.
    true - then go to the next node
    false - then continue loop execution.


```
void *packPackets(short *list)
{
    void *packedList = NULL;
    int packedListSize = 0;
    short *currentPacket = list;

    while (currentPacket != NULL) {
        short packetSize = *currentPacket;

        if (packetSize != 0) {
            // [0] MISTAKE: Didn't provide the correct size to realloc. Correct
            // would be `packedListSize + packetSize`.
            packedList = realloc(packedList, packetSize);

            // Copy the packet.
            memcpy((char *) packedList + packedListSize, (char *) currentPacket + 2,
                    packetSize);
            packedListSize += packetSize;

            // Go to the next packet.
            currentPacket = (short *) ((char *) currentPacket + 2 + packetSize);
        } else {
            // Go to the next node.
            currentPacket = (short *) ((char *) currentPacket + 2);
        }
    }

    return packedList;
}
```


## Work on errors.
```
# ((alley *) (grace->minna))->jessie[2].clara += 960;

R1 = M[SP + 4]  ; load grace
R2 = R1 + 4     ; grace->minna
// TODO: Why do we need to actually load the jessie? Why can't we just `R2 + 16`?
R3 = M[R2 + 16] ; grace->jessie
R4 = M[R3 + 68] ; [2].clara
R3 = .1 M[R2]      ; load `.clara`
M[R3 + 68] = R3 + 960
```
