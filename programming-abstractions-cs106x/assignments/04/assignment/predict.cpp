/*
 * TODO: remove and replace this file header comment
 * You will edit and turn in this file.
 * Remove starter comments and add your own
 * comments on each function and on complex code sections.
 */
#include <iostream>
#include "prototypes.h"
#include "lexicon.h"
#include "set.h"
#include "testing/SimpleTest.h"
using namespace std;

// keypad is a program-wide constant that stores the Map from integer to
// its associated set of possible letters
static const Map<int,Set<char>> keypad = {
    {2, {'a','b','c'} }, {3, {'d','e','f'} }, {4, {'g','h','i'} },
    {5, {'j','k','l'} }, {6, {'m','n','o'} }, {7, {'p','q','r','s'} },
    {8, {'t','u','v'} }, {9, {'w','x','y','z'} } };

void helper(string digits, string guess, Set<string>& suggestions, Lexicon& lex) {
    // base case
    if (guess.length() == digits.length()) {
        if (lex.contains(guess)) {
            suggestions.add(guess);
        }
        return;
    }

    int digit = digits.at(guess.length()) - '0';
    for (char c : keypad.get(digit)) {
        // choose
        guess += c;
        // explore
        if (lex.containsPrefix(guess)) {
            helper(digits, guess, suggestions, lex);
        }
        // un-choose
        guess.pop_back();
    }
}

/*
 * Function takes a string containing digits, then it generates all the
 * possible words using `keypad` from those digits and populates
 * `suggestions` set with valid english words.
 */
void predict(string digits, Set<string>& suggestions, Lexicon& lex) {
    // To prune the branch use `lex.containsPrefix("")`.
    helper(digits, "", suggestions, lex);
}

/* * * * * * Test Cases * * * * * */

/* Test helper function to return shared copy of Lexicon. Use to
 * avoid (expensive) re-load of word list on each test case. */
static Lexicon& sharedLexicon() {
    static Lexicon lex("res/EnglishWords.txt");
    return lex;
}

PROVIDED_TEST("Predict intended words for digit sequence 6263, example from writeup") {
    string digits = "6263";
    Set<string> expected = {"name", "mane", "oboe"};
    Set<string> suggestions;
    predict(digits, suggestions, sharedLexicon());
    EXPECT_EQUAL(suggestions, expected);
}

PROVIDED_TEST("Predict word \"flowers\" for its digit sequence") {
    string digits = "3569377";
    Set<string> suggestions;
    Set<string> expected = {"flowers"};
    predict(digits, suggestions, sharedLexicon());
    EXPECT_EQUAL(suggestions, expected);
}
