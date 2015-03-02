/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports single float property values within an
 * upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class FloatProperty extends AbstractNumericProperty<Float> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<FloatProperty>(
            float.class, NUMBER_FIELD_TYPES_BY_KEY) {

        public FloatProperty createWith(Map<String, String> valuesById) {
            final String[] minMax = minMaxFrom(valuesById);
            return new FloatProperty(nameIn(valuesById), descriptionIn(valuesById), Float.valueOf(minMax[0]),
                    Float.valueOf(minMax[1]), Float.valueOf(numericDefaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for FloatProperty that limits itself to a single value within
     * the specified limits.
     * 
     * @param theName String
     * @param theDescription String
     * @param min float
     * @param max float
     * @param theDefault float
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public FloatProperty(String theName, String theDescription, Float min, Float max, Float theDefault, float theUIOrder) {
        super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);
    }

    /**
     * Constructor for FloatProperty that limits itself to a single value within
     * the specified limits. Converts string arguments into the Float values.
     * 
     * @param theName String
     * @param theDescription String
     * @param minStr String
     * @param maxStr String
     * @param defaultStr String
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public FloatProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
            float theUIOrder) {
        this(theName, theDescription, floatFrom(minStr), floatFrom(maxStr), floatFrom(defaultStr), theUIOrder);
    }

    /**
     * @param numberString String
     * @return Float
     */
    public static Float floatFrom(String numberString) {
        return Float.valueOf(numberString);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Float> type() {
        return Float.class;
    }

    /**
     * Creates an property value of the right type from a raw string.
     * 
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return floatFrom(value);
    }
}
