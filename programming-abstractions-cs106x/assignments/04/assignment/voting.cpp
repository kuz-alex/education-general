/*
 * TODO: remove and replace this file header comment
 * You will edit and turn in this file.
 * Remove starter comments and add your own
 * comments on each function and on complex code sections.
 */
#include <iostream>    // for cout, endl
#include <string>      // for string class
#include "prototypes.h"
#include "testing/SimpleTest.h"
using namespace std;

int countCriticalVotes(int index, int current, int totalVotes, int subsetTotal, Vector<int>& blocks) {
    if (index == blocks.size()) {
        int halfTotalVotes = totalVotes / 2;
        return (subsetTotal <= halfTotalVotes && subsetTotal + current > halfTotalVotes) ? 1 : 0;
    } else {
        int count = 0;
        count += countCriticalVotes(index + 1, current, totalVotes, subsetTotal + blocks[index], blocks);
        count += countCriticalVotes(index + 1, current, totalVotes, subsetTotal, blocks);
        return count;
    }
}

/*
 * Function computes critical power indexes.
 */
Vector<int> computePowerIndexes(Vector<int>& blocks) {
    Vector<int> result;

    int votesToWin = 0;
    for (int i = 0; i < blocks.size(); ++i) {
        votesToWin += blocks[i];
    }

    for (int i = 0; i < blocks.size(); ++i) {
        int current = blocks[i];
        Vector<int> blocksCopy = blocks;

        blocksCopy.remove(i);

        int votesCount = countCriticalVotes(0, current, votesToWin, 0, blocksCopy);
        result.add(votesCount);
    }

    int totalCriticalVotes = 0;
    for (int i = 0; i < result.size(); ++i) {
        totalCriticalVotes += result[i];
    }

    for (int i = 0; i < result.size(); ++i) {
        result[i] = (result[i] * 100) / totalCriticalVotes;
    }

    return result;
}

/* * * * * * Test Cases * * * * * */

PROVIDED_TEST("Test power index, blocks 50-49-1") {
    Vector<int> blocks = {50, 49, 1};
    Vector<int> expected = {60, 20, 20};
    EXPECT_EQUAL(computePowerIndexes(blocks), expected);
}

PROVIDED_TEST("Test power index, blocks Hempshead 1-1-3-7-9-9") {
    Vector<int> blocks = {1, 1, 3, 7, 9, 9};
    Vector<int> expected = {0, 0, 0, 33, 33, 33};
    EXPECT_EQUAL(computePowerIndexes(blocks), expected);
}

PROVIDED_TEST("Test power index, blocks CA-TX-NY 55-38-39") {
    Vector<int> blocks = {55, 38, 29};
    Vector<int> expected = {33, 33, 33};
    EXPECT_EQUAL(computePowerIndexes(blocks), expected);
}

PROVIDED_TEST("Test power index, blocks CA-TX-GA 55-38-16") {
    Vector<int> blocks = {55, 38, 16};
    Vector<int> expected = {100, 0, 0};
    EXPECT_EQUAL(computePowerIndexes(blocks), expected);
}

PROVIDED_TEST("Time power index operation") {
    Vector<int> blocks;
    for (int i = 0; i < 15; i++) {
        blocks.add(randomInteger(1, 10));
    }
    TIME_OPERATION(blocks.size(), computePowerIndexes(blocks));
}
