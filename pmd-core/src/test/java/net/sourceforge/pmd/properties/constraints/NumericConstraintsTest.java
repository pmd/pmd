/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NumericConstraintsTest {

    @Test
    void testInRangeInteger() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);
        assertTrue(constraint.test(1));
        assertTrue(constraint.test(5));
        assertTrue(constraint.test(10));
        assertFalse(constraint.test(0));
        assertFalse(constraint.test(-1));
        assertFalse(constraint.test(11));
        assertFalse(constraint.test(100));
    }

    @Test
    void testInRangeDouble() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        assertTrue(constraint.test(1.0));
        assertTrue(constraint.test(5.5));
        assertTrue(constraint.test(10.0));
        assertFalse(constraint.test(0.0));
        assertFalse(constraint.test(-1.0));
        assertFalse(constraint.test(11.1));
        assertFalse(constraint.test(100.0));
    }

    @Test
    void testPositive() {
        PropertyConstraint<Number> constraint = NumericConstraints.positive();
        assertTrue(constraint.test(1));
        assertTrue(constraint.test(1.5f));
        assertTrue(constraint.test(1.5d));
        assertTrue(constraint.test(100));
        assertFalse(constraint.test(0));
        assertFalse(constraint.test(0.1f));
        assertFalse(constraint.test(0.9d));
        assertFalse(constraint.test(-1));
        assertFalse(constraint.test(-100));
        assertFalse(constraint.test(-0.1f));
        assertFalse(constraint.test(-0.1d));
    }
}
