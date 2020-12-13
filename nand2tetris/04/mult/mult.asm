// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
    @R0
    D=M
    @i
    M=D

    @R1
    D=M
    @n
    M=D

    D=0
    @R2
    M=D

    // If either of the numbers is 0, go straight to the end.
    @R0
    D=M
    @END
    D;JEQ

    @R1
    D=M
    @END
    D;JEQ

(LOOP)
    @i
    D=M
    
    @R2
    M=M+D

    // Decrement `n` by 1 until `n` is 0.
    @n
    D=M-1
    M=D
    @LOOP
    D;JNE


(END)
    @END
    0;JMP
