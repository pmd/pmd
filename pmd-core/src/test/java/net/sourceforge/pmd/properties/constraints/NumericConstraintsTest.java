/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumericConstraintsTest {

    @Test
    void testInRangeInteger() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);
        Assertions.assertTrue(constraint.test(1));
        Assertions.assertTrue(constraint.test(5));
        Assertions.assertTrue(constraint.test(10));
        Assertions.assertFalse(constraint.test(0));
        Assertions.assertFalse(constraint.test(-1));
        Assertions.assertFalse(constraint.test(11));
        Assertions.assertFalse(constraint.test(100));
    }

    @Test
    void testInRangeDouble() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        Assertions.assertTrue(constraint.test(1.0));
        Assertions.assertTrue(constraint.test(5.5));
        Assertions.assertTrue(constraint.test(10.0));
        Assertions.assertFalse(constraint.test(0.0));
        Assertions.assertFalse(constraint.test(-1.0));
        Assertions.assertFalse(constraint.test(11.1));
        Assertions.assertFalse(constraint.test(100.0));
    }

    @Test
    void testPositive() {
        PropertyConstraint<Number> constraint = NumericConstraints.positive();
        Assertions.assertTrue(constraint.test(1));
        Assertions.assertTrue(constraint.test(1.5f));
        Assertions.assertTrue(constraint.test(1.5d));
        Assertions.assertTrue(constraint.test(100));
        Assertions.assertFalse(constraint.test(0));
        Assertions.assertFalse(constraint.test(0.1f));
        Assertions.assertFalse(constraint.test(0.9d));
        Assertions.assertFalse(constraint.test(-1));
        Assertions.assertFalse(constraint.test(-100));
        Assertions.assertFalse(constraint.test(-0.1f));
        Assertions.assertFalse(constraint.test(-0.1d));
    }
}
