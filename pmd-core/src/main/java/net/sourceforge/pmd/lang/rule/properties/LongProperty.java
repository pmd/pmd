/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports the single Long property values within an
 * upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class LongProperty extends AbstractNumericProperty<Long> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<LongProperty>(
            Long.class, NUMBER_FIELD_TYPES_BY_KEY) {

        public LongProperty createWith(Map<String, String> valuesById) {
            final String[] minMax = minMaxFrom(valuesById);
            return new LongProperty(nameIn(valuesById), descriptionIn(valuesById), Long.valueOf(minMax[0]),
                    Long.valueOf(minMax[1]), Long.valueOf(numericDefaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for LongProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param min Long
     * @param max Long
     * @param theDefault Long
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }

    /**
     * Constructor for LongProperty that limits itself to a single value within
     * the specified limits. Converts string arguments into the Long values.
     * 
     * @param theName String
     * @param theDescription String
     * @param minStr String
     * @param maxStr String
     * @param defaultStr String
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public LongProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
            float theUIOrder) {
        this(theName, theDescription, longFrom(minStr), longFrom(maxStr), longFrom(defaultStr), theUIOrder);
    }

    /**
     * @param numberString String
     * @return Long
     */
    public static Long longFrom(String numberString) {
        return Long.valueOf(numberString);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Long> type() {
        return Long.class;
    }

    /**
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return longFrom(value);
    }
}
