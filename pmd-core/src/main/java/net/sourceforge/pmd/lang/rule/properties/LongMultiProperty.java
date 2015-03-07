/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports multiple Long property values within an
 * upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class LongMultiProperty extends AbstractMultiNumericProperty<Long[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<LongMultiProperty>(
            Long[].class, NUMBER_FIELD_TYPES_BY_KEY) {

        public LongMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Long[] defaultValues = longsIn(defaultValueIn(valuesById), delimiter);
            return new LongMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Long.parseLong(minMax[0]),
                    Long.parseLong(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for LongProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param min Long
     * @param max Long
     * @param theDefaults Long[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public LongMultiProperty(String theName, String theDescription, Long min, Long max, Long[] theDefaults,
            float theUIOrder) {
        super(theName, theDescription, min, max, theDefaults, theUIOrder);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Long[]> type() {
        return Long[].class;
    }

    /**
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return Long.valueOf(value);
    }

    /**
     * Returns an array of the correct type for the receiver.
     * 
     * @param size int
     * @return Object[]
     */
    protected Object[] arrayFor(int size) {
        return new Long[size];
    }
}
