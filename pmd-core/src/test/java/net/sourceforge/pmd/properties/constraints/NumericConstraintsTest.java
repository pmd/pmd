/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import org.junit.Assert;
import org.junit.Test;

public class NumericConstraintsTest {

    @Test
    public void testInRangeInteger() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);
        Assert.assertTrue(constraint.test(1));
        Assert.assertTrue(constraint.test(5));
        Assert.assertTrue(constraint.test(10));
        Assert.assertFalse(constraint.test(0));
        Assert.assertFalse(constraint.test(-1));
        Assert.assertFalse(constraint.test(11));
        Assert.assertFalse(constraint.test(100));
    }

    @Test
    public void testInRangeDouble() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        Assert.assertTrue(constraint.test(1.0));
        Assert.assertTrue(constraint.test(5.5));
        Assert.assertTrue(constraint.test(10.0));
        Assert.assertFalse(constraint.test(0.0));
        Assert.assertFalse(constraint.test(-1.0));
        Assert.assertFalse(constraint.test(11.1));
        Assert.assertFalse(constraint.test(100.0));
    }

    @Test
    public void testPositive() {
        PropertyConstraint<Number> constraint = NumericConstraints.positive();
        Assert.assertTrue(constraint.test(1));
        Assert.assertTrue(constraint.test(1.5f));
        Assert.assertTrue(constraint.test(1.5d));
        Assert.assertTrue(constraint.test(100));
        Assert.assertFalse(constraint.test(0));
        Assert.assertFalse(constraint.test(0.1f));
        Assert.assertFalse(constraint.test(0.9d));
        Assert.assertFalse(constraint.test(-1));
        Assert.assertFalse(constraint.test(-100));
        Assert.assertFalse(constraint.test(-0.1f));
        Assert.assertFalse(constraint.test(-0.1d));
    }
}
