/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that support float property values within an upper
 * and lower boundary.
 * 
 * @author Brian Remedios
 */
public class FloatMultiProperty extends AbstractMultiNumericProperty<Float[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<FloatMultiProperty>(
            Float[].class, NUMBER_FIELD_TYPES_BY_KEY) {

        public FloatMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Float[] defaultValues = floatsIn(numericDefaultValueIn(valuesById), delimiter);
            return new FloatMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Float.parseFloat(minMax[0]),
                    Float.parseFloat(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for FloatProperty that configures it to accept multiple
     * values and any number of defaults.
     * 
     * @param theName String
     * @param theDescription String
     * @param min Float
     * @param max Float
     * @param defaultValues Float[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues,
            float theUIOrder) {
        super(theName, theDescription, min, max, defaultValues, theUIOrder);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Float[]> type() {
        return Float[].class;
    }

    /**
     * Creates an property value of the right type from a raw string.
     * 
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return Float.valueOf(value);
    }

    /**
     * Returns an array of the correct type for the receiver.
     * 
     * @param size int
     * @return Object[]
     */
    protected Object[] arrayFor(int size) {
        return new Float[size];
    }
}
