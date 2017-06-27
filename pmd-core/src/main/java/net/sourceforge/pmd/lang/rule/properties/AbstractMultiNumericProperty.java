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

    private final Number lowerLimit;
    private final Number upperLimit;


    /**
     * Constructor for a multi-valued numeric property using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param theDefault     List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public AbstractMultiNumericProperty(String theName, String theDescription, Number min, Number max, List<T> theDefault,
                                        float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);

        if (min.doubleValue() > max.doubleValue()) {
            throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
        }

        lowerLimit = min;
        upperLimit = max;
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
    protected String valueErrorFor(T value) {

        double number = value.doubleValue();

        if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue()) {
            return value + " is out of range " + AbstractNumericProperty.rangeString(lowerLimit, upperLimit);
        }

        return null;
    }


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);
        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }
}
