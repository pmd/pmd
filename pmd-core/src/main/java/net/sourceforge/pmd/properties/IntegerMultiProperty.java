/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.INTEGER_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Multi-valued integer property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Integer>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Integer>(Integer.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public IntegerMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean
                isDefinedExternally) {
                String[] minMax = minMaxFrom(valuesById);
                char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
                List<Integer> defaultValues = ValueParserConstants.parsePrimitives(defaultValueIn(valuesById), delimiter, INTEGER_PARSER);
                return new IntegerMultiProperty(nameIn(valuesById),
                                                descriptionIn(valuesById),
                                                INTEGER_PARSER.valueOf(minMax[0]),
                                                INTEGER_PARSER.valueOf(minMax[1]),
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
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                Integer[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                 List<Integer> defaultValues, float theUIOrder, boolean isDefinedExternally) {

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
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                List<Integer> defaultValues, float theUIOrder) {

        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Integer> type() {
        return Integer.class;
    }


    @Override
    protected Integer createFrom(String toParse) {
        return Integer.valueOf(toParse);
    }
}
