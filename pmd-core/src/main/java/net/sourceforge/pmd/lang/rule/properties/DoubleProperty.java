/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that support single double-type property values
 * within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractNumericProperty<Double> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<DoubleProperty>(
            Double.class, NUMBER_FIELD_TYPES_BY_KEY) {

        public DoubleProperty createWith(Map<String, String> valuesById) {
            final String[] minMax = minMaxFrom(valuesById);
            return new DoubleProperty(nameIn(valuesById), descriptionIn(valuesById), Double.valueOf(minMax[0]),
                    Double.valueOf(minMax[1]), Double.valueOf(numericDefaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for DoubleProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param min double
     * @param max double
     * @param theDefault double
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault,
            float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }

    /**
     * Constructor for DoubleProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param minStr String
     * @param maxStr String
     * @param defaultStr String
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public DoubleProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
            float theUIOrder) {
        this(theName, theDescription, doubleFrom(minStr), doubleFrom(maxStr), doubleFrom(defaultStr), theUIOrder);
    }

    /**
     * @param numberString String
     * @return Double
     */
    public static Double doubleFrom(String numberString) {
        return Double.valueOf(numberString);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Double> type() {
        return Double.class;
    }

    /**
     * Deserializes a string into its Double form.
     * 
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return doubleFrom(value);
    }
}
