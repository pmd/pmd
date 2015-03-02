/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorFields.MAX;
import static net.sourceforge.pmd.PropertyDescriptorFields.MIN;

import java.util.Map;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Maintains a pair of boundary limit values between which all values managed by
 * the subclasses must fit.
 * 
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractNumericProperty<T> extends AbstractScalarProperty<T> implements
        NumericPropertyDescriptor<T> {

    private Number lowerLimit;
    private Number upperLimit;

    public static final Map<String, Boolean> NUMBER_FIELD_TYPES_BY_KEY = BasicPropertyDescriptorFactory
            .expectedFieldTypesWith(new String[] { MIN, MAX }, new Boolean[] { Boolean.TRUE, Boolean.TRUE });

    /**
     * 
     * @param theName
     * @param theDescription
     * @param lower
     * @param upper
     * @param theDefault
     * @param theUIOrder
     * @throws IllegalArgumentException
     */
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
     * @return The minimum value.
     * @see net.sourceforge.pmd.NumericPropertyDescriptor#lowerLimit()
     */
    public Number lowerLimit() {
        return lowerLimit;
    }

    /**
     * @return String
     */
    protected String defaultAsString() {
        return defaultValue().toString();
    }

    /**
     * Returns the maximum value that instances of the property can have
     * 
     * @return The maximum value.
     * @see net.sourceforge.pmd.NumericPropertyDescriptor#upperLimit()
     */
    public Number upperLimit() {
        return upperLimit;
    }

    /**
     * @return String
     */
    public String rangeString() {
        StringBuilder sb = new StringBuilder().append('(').append(lowerLimit).append(" -> ").append(upperLimit)
                .append(')');
        return sb.toString();
    }

    /**
     * Returns a string describing any error the value may have when
     * characterized by the receiver.
     * 
     * @param value Object
     * @return String
     */
    protected String valueErrorFor(Object value) {

        double number = ((Number) value).doubleValue();

        if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue()) {
            return value + " is out of range " + rangeString();
        }

        return null;
    }

    /**
     * Method addAttributesTo.
     * 
     * @param attributes Map<String,String>
     */
    protected void addAttributesTo(Map<String, String> attributes) {
        super.addAttributesTo(attributes);

        attributes.put(MIN, lowerLimit.toString());
        attributes.put(MAX, upperLimit.toString());
    }
}
