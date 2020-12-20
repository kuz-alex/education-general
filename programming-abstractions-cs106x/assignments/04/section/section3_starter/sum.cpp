/*
 * CS106B Section Handout Test Harness: Section 3
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
#include "vector.h"
using namespace std;

/*
 * Win some, lose sum (Code Writing)
 * --------------------------------------------------------
 * Write a recursive function named canMakeSum that takes a
 * reference to a Vector<int> and an int target value and
 * returns true if it is possible to have some selection of
 * values from the Vector that sum to the target value.
 */

bool canMakeSumHelper(Vector<int> values, int index, int target);

bool canMakeSum(Vector<int>& values, int target) {
    return canMakeSumHelper(values, 0, target);
}

bool canMakeSumHelper(Vector<int> values, int index, int target) {
    if (index == values.size()) {
        // Base case, in the end of the decision tree.
        return target == 0;
    }

    return canMakeSumHelper(values, index + 1, target - values.get(index)) || canMakeSumHelper(values, index + 1, target);
}

/* * * * * Provided Tests Below This Point * * * * */
PROVIDED_TEST("Simple Test: positive example.") {
    Vector<int> nums = {1,2,3};
    EXPECT(canMakeSum(nums, 4));
}

PROVIDED_TEST("Provided Test: Positive example from handout.") {
    Vector<int> nums = {1,1,2,3,5};
    EXPECT(canMakeSum(nums, 9));
}

PROVIDED_TEST("Provided Test: Negative example from handout"){
    Vector<int> nums = {1,4,5,6};
    EXPECT(!canMakeSum(nums, 8));
}
