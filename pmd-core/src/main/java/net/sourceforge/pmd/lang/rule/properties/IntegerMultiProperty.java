/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports multiple Integer property values within an
 * upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<IntegerMultiProperty>(
            Integer[].class, NUMBER_FIELD_TYPES_BY_KEY) {

        public IntegerMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Integer[] defaultValues = integersIn(numericDefaultValueIn(valuesById), delimiter);
            return new IntegerMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Integer.parseInt(minMax[0]),
                    Integer.parseInt(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for IntegerProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param min Integer
     * @param max Integer
     * @param theDefaults Integer[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max, Integer[] theDefaults,
            float theUIOrder) {
        super(theName, theDescription, min, max, theDefaults, theUIOrder);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Integer[]> type() {
        return Integer[].class;
    }

    /**
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return Integer.valueOf(value);
    }

    /**
     * @param size int
     * @return Object[]
     */
    protected Object[] arrayFor(int size) {
        return new Integer[size];
    }
}
