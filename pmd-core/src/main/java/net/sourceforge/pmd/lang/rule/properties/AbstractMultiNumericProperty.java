/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorField.MAX;
import static net.sourceforge.pmd.PropertyDescriptorField.MIN;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Base class for multi-valued numeric properties.
 *
 * @param <T> The type of number
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
/* default */ abstract class AbstractMultiNumericProperty<T extends Number> extends AbstractMultiValueProperty<T>
    implements NumericPropertyDescriptor<List<T>> {

    private final T lowerLimit;
    private final T upperLimit;


    /**
     * Constructor for a multi-valued numeric property using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param lower          Minimum value of the property
     * @param upper          Maximum value of the property
     * @param theDefault     List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if lower > upper, or one of them is null, or one of the defaults is not between
     * the bounds
     */
    AbstractMultiNumericProperty(String theName, String theDescription, T lower, T upper, List<T> theDefault,
                                 float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);

        lowerLimit = lower;
        upperLimit = upper;

        checkNumber(lower);
        checkNumber(upper);

        if (lower.doubleValue() > upper.doubleValue()) {
            throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
        }


    }


    private void checkNumber(T number) {
        String error = valueErrorFor(number);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }


    @Override
    protected String valueErrorFor(T value) {

        double number = value.doubleValue();

        if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue()) {
            return value + " is out of range " + AbstractNumericProperty.rangeString(lowerLimit, upperLimit);
        }

        return null;
    }


    @Override
    public Number lowerLimit() {
        return lowerLimit;
    }


    @Override
    public Number upperLimit() {
        return upperLimit;
    }


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);
        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }


    @Override
    /* default */ PropertyDescriptorWrapper<List<T>> getWrapper() {
        return new MultiValueNumericPropertyDescriptorWrapper<>(this);
    }
}
