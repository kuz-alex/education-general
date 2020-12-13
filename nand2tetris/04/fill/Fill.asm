// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(LOOP)
    // Create a copy of symbol SCREEN.
    @SCREEN
    D=A
    @screen_ptr
    M=D

    // There are 256 rows on the screen.
    @255
    D=A
    @row_i
    M=D

(SCREEN_ROW_LOOP_START)
    // Each screen row consists of 32 registers.
    @31
    D=A // `A` register is the only way to have numberic constant.
    @register_i
    M=D

(REGISTER_LOOP_START)
    @KBD
    D=M
    @PAINT_BLACK
    D;JNE

(PAINT_WHITE)
    // Paint white (using pointers).
    @register_i
    D=M
    @screen_ptr
    A=M+D
    M=0

    @PAINT_END
    0;JMP

(PAINT_BLACK)
    // Paint black (using pointers).
    @register_i
    D=M
    @screen_ptr
    A=M+D
    // We use `-1` because we want to flip all the bits to `1` in the register.
    M=-1

(PAINT_END)
    // Decrement `register_i`.
    @register_i
    M=M-1

    // if register_i <= 0 goto start.
    D=M
    @REGISTER_LOOP_START
    D;JGE

    // Increment the screen pointer before going to the start of a "screen row" loop.
    @32
    D=A
    @screen_ptr
    M=M+D

    // Jump to start of a row loop.
    @row_i
    M=M-1
    D=M
    @SCREEN_ROW_LOOP_START
    D;JGE

    @LOOP
    0;JMP
