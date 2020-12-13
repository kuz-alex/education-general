#include <stdio.h>
#include <string.h>

// =========================== Problem #1
void problem01()
{
    /* === Overflowing integer types === */
    /*
     * When you're overflowing a short, there's no errors reported. However if
     * you assign an overflowing number to a short it will give a warning that
     * you're using an int.
     */
    unsigned short k = 65525;
    unsigned short k_overflow = 65526;

    unsigned short new = k + k_overflow;
    printf("Short is: %d and %d \n", k, new);

    // Show how we truncate bit pattern when assigning to a smaller storage.
    int bigger_int = 1038;
    char smaller_char = (char) bigger_int;
    /*
    (gdb) x/tw &bigger_int 
    0x7fffffffde70:	00000000000000000000010000001110
    (gdb) x/dw &bigger_int 
    0x7fffffffde70:	1038

    (gdb) x/tb &smaller_char 
    0x7fffffffde5e:	00001110
    (gdb) x/db &smaller_char 
    0x7fffffffde5e:	14
    */

    // Now we test storing a smaller number in a bigger storage.
    char c_overflow = (char) 128;
    short s = (short) c_overflow;
    short s_normal = 128;
    short s_negative = -128;

    /* Result:
        (gdb) x/th &s
        0x7fffffffde8a:	1111111110000000
        (gdb) x/dh &s 
        0x7fffffffde8e:	-128
        (gdb) x/th &s_normal
        0x7fffffffde8c:	0000000010000000
        (gdb) x/th &s_negative
        0x7fffffffde8e:	1111111110000000

    Note: Overflowed char assigned to short gets interpreted as `-128`, not 128.
    C preserves the sign bit (which makes sense).
    */


    /* === Using bit operations to determine the reminder of a number when divided by 4. === */

    /*
     * In other words, for a number 4 with a bit pattern 0100, we want to `OR`
     * the numbers of the source number starting with the 3rd bit. Whatever on
     * the right of the 3rd bit will be a reminder.
     * Example: If we were to get remineder of 9/4, we would do:
     *     `1001 AND 0011` and get `0001` which is 1, a correct reminder.
     * 
     * Now we do that in the actual code:
     */
    int get_reminder(int divident, int divisor)
    {
        return divident & (divisor - 1);
    }

    int reminder_result = get_reminder(9, 4);
    /*
    (gdb) x/tw &reminder_result 
    0x7fffffffde8c:	00000000000000000000000000000001
    (gdb) x/dw &reminder_result 
    0x7fffffffde8c:	1 */
    reminder_result = get_reminder(573, 16);
    /*
    (gdb) x/tw &reminder_result 
    0x7fffffffde8c:	00000000000000000000000000001101
    (gdb) x/dw &reminder_result 
    0x7fffffffde8c:	13 */
    reminder_result = get_reminder(55, 4);
    /*
    (gdb) x/tw &reminder_result 
    0x7fffffffde8c:	00000000000000000000000000000011
    (gdb) x/dw &reminder_result 
    0x7fffffffde8c:	3 */


    /* === Converting numbers using binary operations === */
    char n = 8;
    char n_c = ~n;
    // Using bit operations to covert a number to negative and back.
    char n_negative = ~n + 1;
    char n_positive = ~(n_negative - 1);

    /* Result:
        (gdb) x/tb &n
        0x7fffffffde81:	00001000
        (gdb) x/db &n
        0x7fffffffde81:	8

        (gdb) x/tb &n_c
        0x7fffffffde82:	11110111
        (gdb) x/db &n_c
        0x7fffffffde82:	-9

        (gdb) x/tb &n_negative 
        0x7fffffffde83:	11111000
        (gdb) x/db &n_negative 
        0x7fffffffde83:	-8

        (gdb) x/tb &n_positive 
        0x7fffffffde6f:	00001000
        (gdb) x/db &n_positive 
        0x7fffffffde6f:	8
    */

    /* === Encrypting and decrypting password with XOR operator === */
    int simple_xor_encryption(int password, int key)
    {
        return password ^ key;
    }
    int key = 1902837491;
    int encrypted_password = simple_xor_encryption(555555, key);
    printf("Encrypted: %d, decrypted: %d.\n", encrypted_password, encrypted_password ^ key);
    // If we run the program twice in a row with the same key, it will just
    // return the original password.
}


// =========================== Problem #2
/* Text answer:
This implies that we cannot safely use those extended ASCII character, if
browser doens't help us with that. I think it's a browser's job to make the
characters the same for every operating system.

The same goes for emails, email clients probably should handle that.

I remember having problems with some coding project files that I moved from
windows, only characters from 0 to 127 are savfe, it makes sense. Extended
character are different on different systems.
*/

// =========================== Problem #3
void problem03()
{
    // = Inspect binary patterns.
    char st[4] = "hi!";
    int i = 3;
    /*
    (gdb) x/dw st
    0x7fffffffde84:	2189672
    (gdb) x/tw st
    0x7fffffffde84:	00000000001000010110100101101000

    (gdb) x/fw &i
    0x7fffffffde74:	4.20389539e-45
    */

    // = Copy contents of a 4-byte struct as it were a float.
    typedef struct testeroni {
        int contents;
    } testeroni;

    testeroni t;
    t.contents = 15;
    /*
    (gdb) x/dw &t
    0x7fffffffde78:	15
    (gdb) x/tw &t
    0x7fffffffde78:	00000000000000000000000000001111
    (gdb) x/fw &t
    0x7fffffffde78:	2.1019477e-44
    */

    // = Copy 4 characters of a string to an integer variable.
    memcpy(&i, &st, 4);
    /*
    (gdb) x/dw &i
    0x7fffffffde74:	2189672
    (gdb) x/tw &i
    0x7fffffffde74:	00000000001000010110100101101000
    */

    // = Determine whether the architecture is big or little-endian.
    int check = 1;
    /*
    (gdb) x/tw &check
    0x7fffffffde80:	00000000000000000000000000000001

    Our system is big-endian.
    */
}

// =========================== Problem #4
/* Text answer:

   (a)
   int *first;
   int *second;

   *second = first + 1;

   (b)
   // Create a pointer to array of a single integer (4 bytes in memory).
   int arr[1];

   // arr[2] it's how we access the Saved PC in M[SP + 4].
   arr[2] = (int *) ((char *) arr + 4);
*/

// =========================== Problem #5
/*
void *findLinkedList()
{
    // `void *kHeapStart, int kHeapSize` is in the global scope.
    for (int i = 0; i < kHeapSize; ++i) {
        struct list *guess = (struct list *) ((char *) kHeapStart + i);

        if (
            guess->data == 1 &&
            (*guess->next)->data == 2 &&
            (*(*guess->next)->next)->data == 3
        ) {
            return (void *) guess;
        }
    }

    return NULL;
}
*/

// =========================== Problem #6
/* Text answer:
a)
    ```
    # char bubs[4];
    SP = SP - 4     ; space for locals

    # bubs[*bubs] = *(marzipan->thecheat[strongmad.coachz]);
    R1 = M[SP]      ; load bubs
    R2 = M[SP + 8]  ; load strongmad.coachz

    // When do we dereference marzipan and when `thecheat[R2]
    R3 = R2 * 4     ; calculate offset (R2 * sizeof(short *))
    R4 = M[R3]      ; load marzipan->thecheat[R3]
    R4 = M[R4]      ; dereference `thecheat`

    M[SP] = R4      ; store in bubs

    # ((homestarrunner *) strongbad.thecheat)->strongbad += *(int *) bubs;
    R1 = M[SP + 12] ; load `strongbad.thecheat`
    R2 = R1 + 12    ; Pretend it's a `homestarrunner` pointer and get address
                    ; of `strongbad`

    R3 = M[R2]      ; load `((homestarrunner *) strongbad.thecheat)->strongbad`
    R4 = .4 M[SP]   ; load `*(int *) bubs`
    M[R2] = R3 + R4 ; store

    SP = SP + 4;
    ```
b)
    ```
    // In C, reference in parameter is just automatically dereferenced pointer.

    # return (**puppetthing(&mrshmallow, *marshie)).strongbad;

    R1 = M[SP + 4]  ; load *marshie
    R2 = M[SP + 8]  ; load &mrshmallow

    SP - 8          ; space for function parameters

    M[SP] = R1      ;
    M[SP + 4] = R2  ; I'm not sure about this line. Since it's a parameter reference, I should dereference it at all times, maybe not when calling a function though. No dereferencing here.

    CALL <puppetthing>
    SP + 8          ; clean up space.

    R1 = M[RV]      ; dereference RV to get `homestarrunner *`.
    ; if we dereference it one more time `R1 = M[R1]` we will get to coachz
    ; (1st struct `int` field). That's not exactly what we want.
    R3 = M[R3]      ; double dereference of return value.
    RV = M[R3 + 12] ;
    RET             ;
    ```
*/

// =========================== Problem #7
/*  Text answer:

# marvin.zaphod[100] = deepthought[*marvin.ford];
R1 = M[SP + 8]      ; load `*marvin.ford`
R2 = R1 * 4         ; calculate offset for `int *deepthought`
R3 = M[SP + 36]     ; load an address `* deepthought`
R4 = R3 + R2        ; calculate address of `deepthought[R2]`

R5 = M[R4]          ; load int of `deepthought[R2]`.
M[SP + 244] = .2 R5 ; flush to `marvin.zaphod[100]`.

# ((galaxy *) ((galaxy *) marvin.zaphod)->ford)->trillian = **marvin.arthur;
R1 = SP + 24        ; calculate offset of `marvin.zaphod`, pretend it's a galaxy pointer.
R1 = R1 + 4         ; calculate offset of `->ford`
R1 = R1 + 16        ; calculate offset of `->trillian`

R2 = M[SP + 4]      ; load address `*marvin.arthur`
R2 = M[R2]          ; load `*marvin.arthur`
R2 = .2 M[R2]       ; load `**marvin.arthur`

M[R1] = R2          ; store **marvin.arthur

# return hitchhikersguide(&marvin + 1, marvin.arthur)->arthur[10];
R1 = SP + 36        ; load `&marvin + 1`, (`SP + 4 + sizeof(galaxy) (32) `)
R2 = M[SP + 4]      ; load `marvin.arthur`
SP = SP - 8         ; space for parameters
M[SP] = R1          ;
M[SP + 4] = R2      ;
CALL <hitchhikersguide>             ;
RV = M[RV]          ; load `->arthur`
RV = M[RV + 40]     ; load `arthur[40]` (RV + 10 * sizeof(short **))
SP = SP + 8         ; clean up
RET                 ;
*/

// =========================== Problem #8
/*  Text Answer:

# obiwan.yoda += macewindu->council[40];
R1 = M[SP + 8]      ; load address `* macewindu`
R1 = .2 M[R1 + 88]  ; load `macewindu->council[40]` (+ 8 + (40 * 2))

R2 = SP + 28        ; offset for `obiwan.yoda`
R3 = R1 * 2         ; calculate offset for the short pointer
M[R2] = M[R2] + R3  ; store `obiwan.yoda + short array offset`

# return obiwan.anakin((short *) &obiwan, *this);
R1 = M[SP + 16]     ; load a function pointer
R2 = SP + 12        ; load `*obiwan`
R3 = M[SP + 4]      ; load `*this`

SP = SP - 12        ; space for parameters

M[SP] = R3          ; store address of `this` as 1st parameter (because it's a method of a class)
M[SP + 4] = R2      ; store address of `*obiwan` as 2nd parameter
M[SP + 8] = R3      ; store address of `this` as 3rd parameter
Call <R1>           ;

SP = SP + 12        ;
RET
*/

int main()
{
    problem01();
    problem03();

    return 0;
}
