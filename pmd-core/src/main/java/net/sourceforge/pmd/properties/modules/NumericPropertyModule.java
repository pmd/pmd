/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.modules;

import static net.sourceforge.pmd.properties.PropertyDescriptorField.MAX;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.MIN;

import java.util.Map;

import net.sourceforge.pmd.properties.PropertyDescriptorField;


/**
 * Common utilities for implementations of numeric property descriptors.
 *
 * @author Clément Fournier
 */
@Deprecated
public class NumericPropertyModule<T extends Number> {

    private final T lowerLimit;
    private final T upperLimit;


    public NumericPropertyModule(T lowerLimit, T upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;

        checkNumber(lowerLimit);
        checkNumber(upperLimit);

        if (lowerLimit.doubleValue() > upperLimit.doubleValue()) {
            throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
        }
    }


    public void checkNumber(T number) {
        String error = valueErrorFor(number);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }


    public String valueErrorFor(T value) {

        if (value == null) {
            return "Missing value";
        }

        double number = value.doubleValue();

        if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue()) {
            return value + " is out of range " + rangeString(lowerLimit, upperLimit);
        }

        return null;
    }


    public T getLowerLimit() {
        return lowerLimit;
    }


    public T getUpperLimit() {
        return upperLimit;
    }


    public void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }


    /**
     * Returns a string representing the range defined by the two bounds.
     *
     * @param low Lower bound
     * @param up  Upper bound
     *
     * @return String
     */
    private static String rangeString(Number low, Number up) {
        return "(" + low + " -> " + up + ")";
    }


}
