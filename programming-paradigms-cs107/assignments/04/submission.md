## Problem 1.

```
# frozone += elastigirl.superboy[*frozone];
R1 = M[SP + 4]     ; get `frozone` pointer.
R2 = .2 M[R1]      ; dereference and get value from `frozone`.
R3 = R2 * 1        ; calculate the offset. We multiply by 1 because it's a char array.

R4 = SP + 20       ; location of `elastigirl.superboy`.
R5 = R4 + R3       ; `elastigirl.superboy` + offset.
R6 = R1 + R5       ; `short *frozone` pointer + offset.
M[SP + 4] = R6     ; update the frozone pointer with a new address. 

# ((superhero *) ((superhero *) elastigirl.dash[0])->dash)->violet) = 400;
# We're calculating offset starting with a value from `elastigirl.dash[0]`, which is a char pointer.
R1 = M[SP + 12]    ; retrieve a char pointer from `elastigirl.dash[0]`,
                     pretend R1 points to a `superhero` struct.
R1 = R1 + 4        ; `+ ->dash`
R1 = R1 + 0        ; `+ ->violet`

M[R1] = 400        ; after calculating the address (R1), we store 400 there.

# `return *pixar(&elastigirl) + 10;`

# Since it's `&elastigirl`, then we want to allocate a new `superhero` pointer
# and store a pointer of a stack allocated `elastigirl` there.

R1 = SP + 8         ; address of `elastigirl`.
SP = SP - 4         ; make space for parameters `(elastigirl *)`.
M[SP] = R1          ; copy an address of `elastigirl` to a space allocated for
                      a function parameter.
CALL <pixar>        ;
SP = SP + 4         ; remove params after function returns

R2 = M[RV] + 10     ; dereference a return value and increment it by 10.
RV = R2             ;
RET                 ;
```

I got two errors in lines 1 & 3 with pointer offsets, when working with any
offsets, don't forget to multiply it by the `sizeof(type)`.

## Problem 2, p1.

```
# `conformity[*conformity] = 0;
R1 = M[SP + 4]      ; get `conformity` pointer.
R2 = .2 M[R1]       ; dereference `conformity` and store its value.
R3 = R2 * 2         ; calculate the offset in the `short` array.
R4 = R1 + R3        ; pointer arithmetic: `(short *) conformity + offset`.
M[R4] = 0           ;

# `gooddeeds += ((struct human *) (gooddeeds->henrietta[0].emmyjo))->doug;
R1 = M[SP + 8]      ; get `gooddeeds` pointer.
R2 = R1 + 12        ; address of `gooddeeds->henrietta`.
R3 = M[R2]          ; dereference (load) `gooddeeds->henrietta[0]`.
R4 = R3 + 4         ; `R3.emmyjo`.
R5 = R4 + 0         ; `((struct human *) R4)->doug`.
R5 = M[R5]          ; load `->doug`.


R6 = R5 * 16        ; offset = `R5 * sizeof(struct human)`.
R7 = R1 + R6        ; add offset to an address.
M[SP + 8] = R7      ; update `gooddeeds` pointer.

# return AskingQuestions((struct human *) &gooddeeds);
R1 = SP + 8         ; address of `gooddeeds`.
SP = SP - 4         ; allocate space for `(human *)` parameter.
M[SP] = R1          ; set a pointer to `gooddeeds` to a newly allocated space.
CALL <AskingQuestions>
SP = SP + 4         ; clean up.
RET
```

## Problem 2, part 2.

```
static struct human **AskingQuestions(struct human *heroes)
{
    struct human manners;
    struct human *remainingCalm;

    manners.doug = manners.emmyjo[0];

    if (*heroes == NULL) {
    }

    remainingCalm = ((struct other *) remainingCalm)->charlie->emmyjo;

    return &((struct other *) remainingCalm)->henrietta;
}
```
