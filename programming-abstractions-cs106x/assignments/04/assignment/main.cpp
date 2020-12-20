#include <iostream>
#include "console.h"
#include "testing/SimpleTest.h"
#include "prototypes.h"
using namespace std;

/*
 * You are free to edit the main in any way that works
 * for your testing/debugging purposes.
 * We will supply our own main() during grading
 */

int main() {
    if (runSimpleTests(SELECTED_TESTS)) {
        return 0;
    }
    cout << "All done, exiting" << endl;
    return 0;
}


// Do not remove or edit this test. It is here to to confirm that your code
// conforms to the expected function prototypes need for grading
PROVIDED_TEST("Confirm function prototypes") {
    bool execute = false;
    if (execute) {
        string str;
        Set<string> s;
        Lexicon lex;
        predict(str, s, lex);

        Grid<char> g;
        scoreBoard(g, lex);

        Vector<int> p;
        p = computePowerIndexes(p);
    }
}
