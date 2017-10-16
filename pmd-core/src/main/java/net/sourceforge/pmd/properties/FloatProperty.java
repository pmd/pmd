/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.FLOAT_PARSER;

import java.util.Map;

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
            public FloatProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                final String[] minMax = minMaxFrom(valuesById);
                return new FloatProperty(nameIn(valuesById),
                                         descriptionIn(valuesById),
                                         FLOAT_PARSER.valueOf(minMax[0]),
                                         FLOAT_PARSER.valueOf(minMax[1]),
                                         FLOAT_PARSER.valueOf(defaultValueIn(valuesById)),
                                         0f,
                                         isDefinedExternally);
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
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated will be removed in 7.0.0
     */
    public FloatProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
                         float theUIOrder) {
        this(theName, theDescription, FLOAT_PARSER.valueOf(minStr),
             FLOAT_PARSER.valueOf(maxStr), FLOAT_PARSER.valueOf(defaultStr), theUIOrder, false);
    }


    /** Master constructor. */
    private FloatProperty(String theName, String theDescription, Float min, Float max, Float theDefault,
                          float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, min, max, theDefault, theUIOrder, isDefinedExternally);
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
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     */
    public FloatProperty(String theName, String theDescription, Float min, Float max, Float theDefault,
                         float theUIOrder) {
        this(theName, theDescription, min, max, theDefault, theUIOrder, false);
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
