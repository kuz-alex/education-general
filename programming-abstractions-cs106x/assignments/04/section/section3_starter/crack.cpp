/*
 * CS106B Section Handout Test Harness: Section 2
 * ----------------------------------------------
 * These problems have been galvanized from years of
 * section handouts that have been worked on by numerous
 * instructors and TA's. Codified by Chase Davis for CS106B
 * Fall 2020.
 *
 * A huge thank you to Keith Schwarz and Julie Zelenski
 * for creating an amazing testing harness!
 */

#include <iostream>
#include "testing/SimpleTest.h"
#include "testing/TextUtils.h"
#include "error.h"
using namespace std;

/*
 * Cracking Passwords (Code Writing)
 * --------------------------------------------------------
 * Write a function crack that takes in the maximum length a
 * site allows for a user's password and tries to find the
 * password into an account by using recursive backtracking to
 * attempt all possible passwords up to that length (inclusive).
 */

bool login(string password) {
    return (password == "aia");
}

string helper(int n, string choice) {
    if (n == 0) {
        return choice; // base case, return the string when we reached the password length.
    }

    for (unsigned char c = 97; c < 123; ++c) {
        string newChoice = choice;
        newChoice.push_back(c);

        string guess = helper(n - 1, newChoice);
        cout << "Guessing the password: " << guess << ", n is: " << n - 1 << endl;
        if (login(guess)) {
            return guess;
        }
    }
    return "";
}

string crackingPasswords(int n) {
    //return crackHelper("", n);
    return helper(n, "");
}


/* * * * * Provided Tests Below This Point * * * * */

PROVIDED_TEST("Provided Test: Example from handout.") {
    EXPECT_EQUAL(crackingPasswords(3), "aia");
}
