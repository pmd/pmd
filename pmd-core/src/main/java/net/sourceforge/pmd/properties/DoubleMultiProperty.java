/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.DOUBLE_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Multi-valued double property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class DoubleMultiProperty extends AbstractMultiNumericProperty<Double> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Double>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Double>(Double.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public DoubleMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                String[] minMax = minMaxFrom(valuesById);
                char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
                List<Double> defaultValues
                    = ValueParserConstants.parsePrimitives(defaultValueIn(valuesById), delimiter, DOUBLE_PARSER);

                return new DoubleMultiProperty(nameIn(valuesById),
                                               descriptionIn(valuesById),
                                               DOUBLE_PARSER.valueOf(minMax[0]),
                                               DOUBLE_PARSER.valueOf(minMax[1]),
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
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               Double[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                                List<Double> defaultValues, float theUIOrder, boolean isDefinedExternally) {
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
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               List<Double> defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Double> type() {
        return Double.class;
    }


    @Override
    protected Double createFrom(String value) {
        return Double.valueOf(value);
    }

}
