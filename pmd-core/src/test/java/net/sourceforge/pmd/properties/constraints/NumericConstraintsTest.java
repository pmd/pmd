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
        Assert.assertNull(constraint.validate(1));
        Assert.assertNull(constraint.validate(5));
        Assert.assertNull(constraint.validate(10));
        Assert.assertNotNull(constraint.validate(0));
        Assert.assertEquals("'-1' should be between 1 and 10", constraint.validate(-1));
        Assert.assertNotNull(constraint.validate(11));
        Assert.assertNotNull(constraint.validate(100));
    }

    @Test
    public void testInRangeDouble() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        Assert.assertNull(constraint.validate(1.0));
        Assert.assertNull(constraint.validate(5.5));
        Assert.assertNull(constraint.validate(10.0));
        Assert.assertNotNull(constraint.validate(0.0));
        Assert.assertNotNull(constraint.validate(-1.0));
        Assert.assertNotNull(constraint.validate(11.1));
        Assert.assertNotNull(constraint.validate(100.0));
    }

    @Test
    public void testPositive() {
        PropertyConstraint<Number> constraint = NumericConstraints.positive();
        Assert.assertNull(constraint.validate(1));
        Assert.assertNull(constraint.validate(1.5f));
        Assert.assertNull(constraint.validate(1.5d));
        Assert.assertNull(constraint.validate(100));
        Assert.assertNotNull(constraint.validate(0));
        Assert.assertEquals("'0.1' should be positive", constraint.validate(0.1f));
        Assert.assertNotNull(constraint.validate(0.9d));
        Assert.assertNotNull(constraint.validate(-1));
        Assert.assertNotNull(constraint.validate(-100));
        Assert.assertNotNull(constraint.validate(-0.1f));
        Assert.assertNotNull(constraint.validate(-0.1d));
    }
}
