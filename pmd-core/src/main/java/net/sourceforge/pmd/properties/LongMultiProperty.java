/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.LONG_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Multi-valued long property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class LongMultiProperty extends AbstractMultiNumericProperty<Long> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Long>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Long>(Long.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public LongMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                String[] minMax = minMaxFrom(valuesById);
                char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
                List<Long> defaultValues = ValueParserConstants.parsePrimitives(defaultValueIn(valuesById), delimiter, LONG_PARSER);
                return new LongMultiProperty(nameIn(valuesById),
                                             descriptionIn(valuesById),
                                             LONG_PARSER.valueOf(minMax[0]),
                                             LONG_PARSER.valueOf(minMax[1]),
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
    public LongMultiProperty(String theName, String theDescription, Long min, Long max,
                             Long[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private LongMultiProperty(String theName, String theDescription, Long min, Long max,
                              List<Long> defaultValues, float theUIOrder, boolean isDefinedExternally) {
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
    public LongMultiProperty(String theName, String theDescription, Long min, Long max,
                             List<Long> defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Long> type() {
        return Long.class;
    }


    @Override
    protected Long createFrom(String value) {
        return Long.valueOf(value);
    }

}
