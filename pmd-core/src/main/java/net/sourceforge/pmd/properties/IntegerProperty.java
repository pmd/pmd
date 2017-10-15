/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.INTEGER_PARSER;

import java.util.Map;

/**
 * Defines a datatype that supports single Integer property values within an
 * upper and lower boundary.
 *
 * @author Brian Remedios
 */
public final class IntegerProperty extends AbstractNumericProperty<Integer> {

    /** Factory. */
    public static final PropertyDescriptorFactory<Integer> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<Integer>(Integer.class, NUMBER_FIELD_TYPES_BY_KEY) {

            @Override
            public IntegerProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                final String[] minMax = minMaxFrom(valuesById);
                return new IntegerProperty(nameIn(valuesById),
                                           descriptionIn(valuesById),
                                           INTEGER_PARSER.valueOf(minMax[0]),
                                           INTEGER_PARSER.valueOf(minMax[1]),
                                           INTEGER_PARSER.valueOf(defaultValueIn(valuesById)),
                                           0f,
                                           isDefinedExternally);
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
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     */
    public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault,
                           float theUIOrder) {
        this(theName, theDescription, min, max, theDefault, theUIOrder, false);
    }


    /** Master constructor. */
    private IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault,
                            float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, min, max, theDefault, theUIOrder, isDefinedExternally);
    }


    @Override
    public Class<Integer> type() {
        return Integer.class;
    }


    @Override
    protected Integer createFrom(String value) {
        return INTEGER_PARSER.valueOf(value);
    }


}
