/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports single Integer property values within an
 * upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class IntegerProperty extends AbstractNumericProperty<Integer> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<IntegerProperty>(
            Integer.class, NUMBER_FIELD_TYPES_BY_KEY) {

        public IntegerProperty createWith(Map<String, String> valuesById) {
            final String[] minMax = minMaxFrom(valuesById);
            return new IntegerProperty(nameIn(valuesById), descriptionIn(valuesById), Integer.valueOf(minMax[0]),
                    Integer.valueOf(minMax[1]), Integer.valueOf(numericDefaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for IntegerProperty that limits itself to a single value
     * within the specified limits.
     * 
     * @param theName String
     * @param theDescription String
     * @param min Integer
     * @param max Integer
     * @param theDefault Integer
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault,
            float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }

    /**
     * Constructor for IntegerProperty that limits itself to a single value
     * within the specified limits. Converts string arguments into the Float
     * values.
     * 
     * @param theName String
     * @param theDescription String
     * @param minStr String
     * @param maxStr String
     * @param defaultStr String
     * @param theUIOrder
     * @throws IllegalArgumentException
     */
    public IntegerProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
            float theUIOrder) {
        this(theName, theDescription, intFrom(minStr), intFrom(maxStr), intFrom(defaultStr), theUIOrder);
    }

    /**
     * @param numberString String
     * @return Integer
     */
    public static Integer intFrom(String numberString) {
        return Integer.valueOf(numberString);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Integer> type() {
        return Integer.class;
    }

    /**
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return intFrom(value);
    }
}
