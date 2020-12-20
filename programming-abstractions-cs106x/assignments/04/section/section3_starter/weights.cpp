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
#include "error.h"
#include "vector.h"
using namespace std;

/*
 * Weights and Balances (Code Writing)
 * --------------------------------------------------------
 * Write a recursive function that determines whether it is
 * possible to measure out the desired target amount with a
 * given set of weights, which is stored in the vector
 * weights.
 */
bool isMeasurableHelper(int target, Vector<int>& weights);

bool isMeasurable(int target, Vector<int>& weights) {
    return isMeasurableHelper(target, weights);
}

bool isMeasurableHelper(int target, Vector<int>& weights) {
    if (weights.isEmpty()) {
        return target == 0;
    }
    int last = weights.get(weights.size() - 1);
    weights.remove(weights.size() - 1);

    // There are three possibilities. We take a weight and:
    // 1. Put it on the left side, adding it to the object.
    // 2. Put it on the right side, subtracting it from the object.
    // 3. Don't put it anywhere at all.
    // Remove the first element.
    bool result = isMeasurableHelper(target - last, weights)
            || isMeasurableHelper(target + last, weights)
            || isMeasurableHelper(target, weights);

    // Un-choose
    weights.add(last);
    return result;
}

/* * * * * Provided Tests Below This Point * * * * */

PROVIDED_TEST("Provided Test: Simple Positive Example"){
    Vector<int> weights = {1, 3};
    EXPECT(isMeasurable(2, weights));
}

PROVIDED_TEST("Provided Test: Simple Negative Example"){
    Vector<int> weights = {1, 3};
    EXPECT(!isMeasurable(5, weights));
}

PROVIDED_TEST("Provided Test: Complex Positive Example"){
    Vector<int> weights = {1, 3, 7};
    EXPECT(isMeasurable(6, weights));
}
