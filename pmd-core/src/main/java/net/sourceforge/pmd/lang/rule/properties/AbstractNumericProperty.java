/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorField.MAX;
import static net.sourceforge.pmd.PropertyDescriptorField.MIN;

import java.util.Map;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Maintains a pair of boundary limit values between which all values managed by
 * the subclasses must fit.
 *
 * @param <T> The type of value.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
/* default */ abstract class AbstractNumericProperty<T extends Number> extends AbstractSingleValueProperty<T>
    implements NumericPropertyDescriptor<T> {

    public static final Map<PropertyDescriptorField, Boolean> NUMBER_FIELD_TYPES_BY_KEY
        = BasicPropertyDescriptorFactory.expectedFieldTypesWith(new PropertyDescriptorField[] {MIN, MAX},
                                                                new Boolean[] {true, true});

    private Number lowerLimit;
    private Number upperLimit;


    protected AbstractNumericProperty(String theName, String theDescription, Number lower, Number upper, T theDefault,
                                      float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);

        if (lower.doubleValue() > upper.doubleValue()) {
            throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
        }

        lowerLimit = lower;
        upperLimit = upper;
    }


    /**
     * Returns the minimum value that instances of the property can have
     *
     * @return The minimum value
     */
    @Override
    public Number lowerLimit() {
        return lowerLimit;
    }


    /**
     * Returns the maximum value that instances of the property can have
     *
     * @return The maximum value.
     */
    @Override
    public Number upperLimit() {
        return upperLimit;
    }


    /**
     * Returns a string describing any error the value may have when
     * characterized by the receiver.
     *
     * @param value Object
     *
     * @return String
     */
    @Override
    protected String valueErrorFor(T value) {

        double number = value.doubleValue();

        if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue()) {
            return value + " is out of range " + rangeString(lowerLimit, upperLimit);
        }

        return null;
    }


    /**
     * Returns a string representing the range defined by the two bounds.
     *
     * @param low Lower bound
     * @param up  Upper bound
     *
     * @return String
     */
    static String rangeString(Number low, Number up) {
        return "(" + low + " -> " + up + ")";
    }


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);
        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }


    @Override
    /* default */ PropertyDescriptorWrapper<T> getWrapper() {
        return new SingleValueNumericPropertyDescriptorWrapper<>(this);
    }

}
