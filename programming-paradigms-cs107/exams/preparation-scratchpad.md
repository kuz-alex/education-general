# Assignment 5: Raw Memory

## Problem 6.a

```
SP = SP - 4     ; space for bubs

// bubs[*bubs] = *(marzipan->thecheat[strongmad.coachz]);
R1 = .1 M[SP]      ; load bubs[0].
R1 = SP + R1       ; prepare `bubs + *bubs`.

R2 = .4 M[SP + 8]  ; load `strongmad.coachz`.
R4 = R2 * 4        ; scale offset by `sizeof(char *)`.

R3 = M[SP + 24]    ; load `marzipan`.
R3 = R3 + 4        ; advance to `marzipan->thecheat` (by incrementing the address).
R3 = R3 + R4       ; advance to `marzipan->thecheat[strongmad.coachz]`.
R3 = M[R3]         ; load `short *`.
R3 = .2 M[R3]      ; load `short`.
M[SP + R1] = .1 R3 ; store `short` in the `bubs[*bubs]`.

// ((homestarrunner *) (strongmad.thecheat))->strongbad += *(int *) bubs;

R3 = .4 M[SP]      ; load `*(int *) bubs`.
R3 = R3 * 4        ; scale by `homestarrunner *`.

R1 = SP + 12       ; advance to `strongmad.thecheat` and pretend it's `homestarrunner *`.
R1 = R1 + 12       ; advance to `->strongbad`
/* Note, we don't dereference a pointer, we just advance it. */
R2 = M[R1]         ; load old value of `homestarrunner **`
R3 = R2 + R3       ; do pointer arithmetic.
M[R1] = R3         ; store result

SP = SP + 4;
RETURN;
```

## Problem 6.b

```
// return (**puppetthing(&mrshmallow, *marshie)).strongbad;
R1 = M[SP + 4]  ; load `*marshie`.
R2 = M[SP + 8]  ; load `&mrshmallow`, it's the 4-byte address.

SP = SP - 8     ; allocate space for parameters.

M[SP] = R2; store `&mrshmallow` param.
M[SP + 4] = R1; store `*marshie` param.

CALL <puppetthing>;
RV = M[RV]
RV = M[RV + 12] ; replace RV with contents of strongbad field relative to R1 address.

SP = SP + 8 ; clean up.
RET;
```


## Problem 20.1

```

Activation record:
20 [elastigirl.superboy[0..3]] char[4]
16 [elastigirl.dash[1]       ] char *
12 [elastigirl.dash[0]       ] char *
08 [elastigirl.violet        ] int
04 [frozone                  ] short *
00 [Saved PC                 ] address         <-- SP


// 1. frozone += elastigirl.superboy[*frozone];
R1 = M[SP + 4] ; load `frozone *`.

R2 = .2 M[R1] ; load value of `frozone *`.
R3 = R2 * 1; calculate offset into superboy array

R4 = SP + 20    ; load address of elastigirl.superboy.
R4 = R4 + R3    ; advance `elastigirl.superboy` by offset.
R4 = .1 M[R4]   ; load value from the address.
R5 = R4 * 2; scale offset into `short` array by 2.

R6 = R1 + R5; do the pointer arithmetic
M[R1] = R6 ; store computer address into the frozone.

// 2. ((superhero *)((superhero *) elastigirl.dash[0])->dash)->violet = 400;

// R1 = M[SP + 12]; load `char*` in `elastigirl.dash[0]` and cast.

R1 = SP + 12; calculate address of `elastigirl.dash[0]` and cast (note we don't dereference the address to get `char *`).
R1 = R1 + 4; advance address by `->dash` and cast.
R1 = R1 + 0; advance address by `->violet`.

M[R1] = 400; flush a constant to the address.

// 3. return *pixar(&elastigirl) + 10;
SP = SP - 4; space for function argument
R1 = SP + 12; compute the address
M[SP] = R1; store `elastigirl` address in the function parameter.
CALL <pixar>;
RV = M[RV]; dereference the returned pointer, load `superhero *`.
R2 = 10 * 16; calculate offset into `superhero` array.
RV = RV + R2; advance an address by offset.

SP = SP + 4; cleanup
RET;
```

## Problem 20.2
```
AR:
08 [gooddeeds ] struct other *
04 [conformity] short *
00 [Saved PC  ] address

// 1. conformity[*conformity] = 0;
R1 = M[SP + 4]; get conformity address
R2 = M[R1]; load conformity value
R3 = R2 * 2; calculate offset into `short` array
R4 = R1 + R3; advance pointer by offset
M[R4] = .2 0; flush 0 into the location.

// 2. gooddeeds += ((struct human *) (gooddeeds->henrietta[0].emmyjo))->doug;
```
