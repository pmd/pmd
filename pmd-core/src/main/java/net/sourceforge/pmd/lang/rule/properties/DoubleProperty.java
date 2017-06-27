/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.DOUBLE_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that support single double-type property values
 * within an upper and lower boundary.
 *
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractNumericProperty<Double> {

    public static final PropertyDescriptorFactory FACTORY // @formatter:off
            = new BasicPropertyDescriptorFactory<Double>(Double.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public DoubleProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                final String[] minMax = minMaxFrom(valuesById);
                return new DoubleProperty(nameIn(valuesById),
                                          descriptionIn(valuesById),
                                          DOUBLE_PARSER.valueOf(minMax[0]),
                                          DOUBLE_PARSER.valueOf(minMax[1]),
                                          DOUBLE_PARSER.valueOf(numericDefaultValueIn(valuesById)),
                                          0f);
            }
        }; // @formatter:on


    /**
     * Constructor that limits itself to a single value within the specified limits.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param theDefault     Default value
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault,
                          float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }


    /**
     * Constructor for DoubleProperty that limits itself to a single value within the specified limits. Converts string
     * arguments into the Double values.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param minStr         Minimum value of the property
     * @param maxStr         Maximum value of the property
     * @param defaultStr     Default value
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     * @deprecated ?
     */
    public DoubleProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
                          float theUIOrder) {
        this(theName, theDescription, doubleFrom(minStr), doubleFrom(maxStr), doubleFrom(defaultStr), theUIOrder);
    }


    /**
     * Parses a String into a Double.
     *
     * @param numberString String to parse
     *
     * @return Parsed Double
     */
    public static Double doubleFrom(String numberString) {
        return Double.valueOf(numberString);
    }


    @Override
    public Class<Double> type() {
        return Double.class;
    }


    @Override
    protected Double createFrom(String value) {
        return doubleFrom(value);
    }
}
