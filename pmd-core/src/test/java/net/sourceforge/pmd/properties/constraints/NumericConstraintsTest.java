/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NumericConstraintsTest {

    @Test
    void testInRangeInteger() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);
        assertNull(constraint.validate(1));
        assertNull(constraint.validate(5));
        assertNull(constraint.validate(10));
        assertNotNull(constraint.validate(0));
        assertEquals("'-1' should be between 1 and 10", constraint.validate(-1));
        assertNotNull(constraint.validate(11));
        assertNotNull(constraint.validate(100));
    }

    @Test
    void testInRangeDouble() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        assertNull(constraint.validate(1.0));
        assertNull(constraint.validate(5.5));
        assertNull(constraint.validate(10.0));
        assertNotNull(constraint.validate(0.0));
        assertNotNull(constraint.validate(-1.0));
        assertNotNull(constraint.validate(11.1));
        assertNotNull(constraint.validate(100.0));
    }

    @Test
    void testPositive() {
        PropertyConstraint<Number> constraint = NumericConstraints.positive();
        assertNull(constraint.validate(1));
        assertNull(constraint.validate(1.5f));
        assertNull(constraint.validate(1.5d));
        assertNull(constraint.validate(100));
        assertNotNull(constraint.validate(0));
        assertEquals("'0.1' should be positive", constraint.validate(0.1f));
        assertNotNull(constraint.validate(0.9d));
        assertNotNull(constraint.validate(-1));
        assertNotNull(constraint.validate(-100));
        assertNotNull(constraint.validate(-0.1f));
        assertNotNull(constraint.validate(-0.1d));
    }
}
