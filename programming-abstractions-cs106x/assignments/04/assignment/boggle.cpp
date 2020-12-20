/*
 * TODO: remove and replace this file header comment
 * You will edit and turn in this file.
 * Remove starter comments and add your own
 * comments on each function and on complex code sections.
 */
#include <iostream>    // for cout, endl
#include <string>      // for string class
#include "prototypes.h"
#include "gridlocation.h"
#include "grid.h"
#include "set.h"
#include "lexicon.h"
#include "testing/SimpleTest.h"
using namespace std;

/*
 * Determines how much points a word is worth based on it's length.
 */
int points(string str) {
    return str.length() - 3;
}

int backtrack(string choice,
              Set<GridLocation>& currentPath,
              GridLocation prevLocation,
              Set<string>& words,
              Grid<char>& board,
              Lexicon& lex) {
    int count = 0;

    Vector<GridLocation> locations;
    if (currentPath.isEmpty()) {
        // We will check every location as the start point.
        for (int i = 0; i < board.numRows(); ++i)
            for (int j = 0; j < board.numRows(); ++j)
                locations.add(GridLocation(i, j));
    } else {
        // We will consider only neighbors of the previous node.
        int row = prevLocation.row;
        int col = prevLocation.col;
        for (GridLocation loc: {GridLocation(row - 1, col - 1),
                                GridLocation(row + 1, col + 1),
                                GridLocation(row + 1, col - 1),
                                GridLocation(row - 1, col + 1),
                                GridLocation(row, col + 1),
                                GridLocation(row, col - 1),
                                GridLocation(row + 1, col),
                                GridLocation(row - 1, col)}) {
            if (board.inBounds(loc)) {
                locations.add(loc);
            }
        }
    }

    for (GridLocation loc : locations) {
        if (currentPath.contains(loc)) {
            continue;
        }

        choice += board.get(loc);

        if (choice.length() >= 4 && lex.contains(choice) && !words.contains(choice)) {
            count += points(choice);
            words.add(choice);
        }

        if (lex.containsPrefix(choice)) {
            currentPath.add(loc);
            count += backtrack(choice, currentPath, loc, words, board, lex);
            currentPath.remove(loc);
        }
        choice.pop_back();
    }

    return count;
}

/*
 * Takes a board and determine the number of points scored in
 * the game of boggle on this board.
 */
int scoreBoard(Grid<char>& board, Lexicon& lex) {
    Set<GridLocation> s;
    Set<string> words;
    int result = backtrack("", s, GridLocation(-1, -1), words, board, lex);
    cout << words << endl;
    return result;
}

/* * * * * * Test Cases * * * * * */

/* Test helper function to return shared copy of Lexicon. Use to
 * avoid (expensive) re-load of word list on each test case. */
static Lexicon& sharedLexicon() {
    static Lexicon lex("res/EnglishWords.txt");
    return lex;
}

PROVIDED_TEST("Load shared Lexicon, confirm number of words") {
    Lexicon lex = sharedLexicon();
    EXPECT_EQUAL(lex.size(), 127145);
}

PROVIDED_TEST("Test point scoring") {
    EXPECT_EQUAL(points("and"), 0);
    EXPECT_EQUAL(points("quad"), 1);
    EXPECT_EQUAL(points("quint"), 2);
    EXPECT_EQUAL(points("sextet"), 3);
    EXPECT_EQUAL(points("seventh"), 4);
    EXPECT_EQUAL(points("supercomputer"), 10);
}

PROVIDED_TEST("Test scoreBoard, board contains no words, score of zero") {
    Grid<char> board = {{'B','C','D','F'}, //no vowels, no words
                        {'G','H','J','K'},
                        {'L','M','N','P'},
                        {'Q','R','S','T'}};
    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 0);
}

PROVIDED_TEST("Test scoreBoard, board contains one word, score of 1") {
    Grid<char> board = {{'C','_','_','_'},
                        {'Z','_','_','_'},
                        {'_','A','_','_'},
                        {'_','_','R','_'}};
    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 1);
}

PROVIDED_TEST("Test scoreBoard, alternate paths for same word, still score of 1") {
    Grid<char> board = {{'C','C','_','_'},
                        {'C','Z','C','_'},
                        {'_','A','_','_'},
                        {'R','_','R','_'}};
    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 1);
}

PROVIDED_TEST("Test scoreBoard, small number of words in corner of board") {
    Grid<char> board = {{'L','I','_','_'},
                        {'M','E','_','_'},
                        {'_','S','_','_'},
                        {'_','_','_','_'}};
    Set<string> words = {"SMILE", "LIMES", "MILES", "MILE", "MIES", "LIME", "LIES", "ELMS", "SEMI"};

    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()),  2 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1);
}

PROVIDED_TEST("Test scoreBoard, full board, small number of words") {
    Grid<char> board = {{'E','Z','R','R'},
                        {'O','H','I','O'},
                        {'N','J','I','H'},
                        {'Y','A','H','O'}};
    Set<string> words = { "HORIZON", "OHIA", "ORZO", "JOHN", "HAJI"};

    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 4 + 1 + 1 + 1 + 1);
}

PROVIDED_TEST("Test scoreBoard, full board, medium number of words") {
    Grid<char> board = {{'O','T','H','X'},
                        {'T','H','T','P'},
                        {'S','S','F','E'},
                        {'N','A','L','T'}};

    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 76);
}

PROVIDED_TEST("Test scoreBoard, full board, large number of words") {
    Grid<char> board = {{'E','A','A','R'},
                        {'L','V','T','S'},
                        {'R','A','A','N'},
                        {'O','I','S','E'}};

    EXPECT_EQUAL(scoreBoard(board, sharedLexicon()), 234);
}
