/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Thrown when a property constraint is violated. Detected while parsing
 * values from XML.
 *
 * @author Clément Fournier
 */
public class ConstraintViolatedException extends IllegalArgumentException {

    private final PropertyConstraint<?> constraint;

    <T> ConstraintViolatedException(PropertyConstraint<T> constraint, T value) {
        super("'" + value + "' " + StringUtils.uncapitalize(constraint.getConstraintDescription()));
        this.constraint = constraint;
    }

    public PropertyConstraint<?> getConstraint() {
        return constraint;
    }

    public String getMessageWithoutValue() {
        return "Value " + StringUtils.uncapitalize(constraint.getConstraintDescription());
    }
}
