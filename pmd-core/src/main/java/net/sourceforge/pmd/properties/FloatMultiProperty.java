/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.FLOAT_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Multi-valued float property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class FloatMultiProperty extends AbstractMultiNumericProperty<Float> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Float>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Float>(Float.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public FloatMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById,
                                                 boolean isDefinedExternally) {
                String[] minMax = minMaxFrom(valuesById);
                char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
                List<Float> defaultValues = ValueParserConstants.parsePrimitives(defaultValueIn(valuesById), delimiter, FLOAT_PARSER);
                return new FloatMultiProperty(nameIn(valuesById),
                                              descriptionIn(valuesById),
                                              FLOAT_PARSER.valueOf(minMax[0]),
                                              FLOAT_PARSER.valueOf(minMax[1]),
                                              defaultValues,
                                              0f,
                                              isDefinedExternally);
            }
        }; // @formatter:on


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param defaultValues  Array of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     */
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                              Float[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                               List<Float> defaultValues, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, min, max, defaultValues, theUIOrder, isDefinedExternally);
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     */
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                              List<Float> defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Float> type() {
        return Float.class;
    }


    @Override
    protected Float createFrom(String value) {
        return Float.valueOf(value);
    }
}
