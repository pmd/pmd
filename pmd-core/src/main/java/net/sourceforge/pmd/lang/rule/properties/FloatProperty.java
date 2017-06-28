/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.ValueParser.FLOAT_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Defines a property type that supports single float property values within an
 * upper and lower boundary.
 *
 * @author Brian Remedios
 */
public final class FloatProperty extends AbstractNumericProperty<Float> {

    /** Factory. */
    public static final PropertyDescriptorFactory<Float> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<Float>(Float.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public FloatProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                final String[] minMax = minMaxFrom(valuesById);
                return new FloatProperty(nameIn(valuesById),
                                         descriptionIn(valuesById),
                                         FLOAT_PARSER.valueOf(minMax[0]),
                                         FLOAT_PARSER.valueOf(minMax[1]),
                                         FLOAT_PARSER.valueOf(numericDefaultValueIn(valuesById)), 0f);
            }
        }; // @formatter:on


    /**
     * Constructor for FloatProperty that limits itself to a single value within the specified limits. Converts string
     * arguments into the Float values.
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
    public FloatProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
                         float theUIOrder) {
        this(theName, theDescription, floatFrom(minStr), floatFrom(maxStr), floatFrom(defaultStr), theUIOrder);
    }


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
    public FloatProperty(String theName, String theDescription, Float min, Float max, Float theDefault,
                         float theUIOrder) {
        super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);
    }


    /**
     * Parses a String into a Float.
     *
     * @param numberString String to parse
     *
     * @return Parsed Float
     */
    public static Float floatFrom(String numberString) {
        return FLOAT_PARSER.valueOf(numberString);
    }


    @Override
    public Class<Float> type() {
        return Float.class;
    }


    @Override
    protected Float createFrom(String value) {
        return FLOAT_PARSER.valueOf(value);
    }
}
