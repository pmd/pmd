/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorFields.MAX;
import static net.sourceforge.pmd.PropertyDescriptorFields.MIN;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.NumericPropertyDescriptor;

/**
 * @param <T>
 *
 * @author Brian Remedios
 */
public abstract class AbstractMultiNumericProperty<T extends Number> extends AbstractMultiValueProperty<T>
    implements NumericPropertyDescriptor<List<T>> {


    private Number lowerLimit;
    private Number upperLimit;


    /**
     * Constructor for AbstractMultiNumericProperty.Object
     *
     * @param theName        String
     * @param theDescription String
     * @param lower          Number
     * @param upper          Number
     * @param theDefault     T
     * @param theUIOrder     float
     */
    public AbstractMultiNumericProperty(String theName, String theDescription, Number lower, Number upper,
                                           List<T> theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);

        if (lower.doubleValue() > upper.doubleValue()) {
            throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
        }

        lowerLimit = lower;
        upperLimit = upper;
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
    protected void addAttributesTo(Map<String, String> attributes) {
        super.addAttributesTo(attributes);
        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }
}
