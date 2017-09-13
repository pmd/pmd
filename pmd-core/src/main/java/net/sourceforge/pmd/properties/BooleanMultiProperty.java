/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.BOOLEAN_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Defines a property type that supports multiple Boolean values.
 *
 * @author Brian Remedios
 */
public final class BooleanMultiProperty extends AbstractMultiValueProperty<Boolean> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Boolean>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Boolean>(Boolean.class) {
            @Override
            public BooleanMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                char delimiter = delimiterIn(valuesById);
                return new BooleanMultiProperty(nameIn(valuesById),
                                                descriptionIn(valuesById),
                                                ValueParserConstants.parsePrimitives(defaultValueIn(valuesById), delimiter, BOOLEAN_PARSER),
                                                0f,
                                                isDefinedExternally);
            }
        }; // @formatter:on


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     */
    public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
        this(theName, theDescription, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private BooleanMultiProperty(String theName, String theDescription, List<Boolean> defaultValues,
                                 float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValues, theUIOrder, isDefinedExternally);
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     */
    public BooleanMultiProperty(String theName, String theDescription, List<Boolean> defaultValues, float theUIOrder) {
        this(theName, theDescription, defaultValues, theUIOrder, false);
    }


    @Override
    protected Boolean createFrom(String toParse) {
        return BOOLEAN_PARSER.valueOf(toParse);
    }


    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

}
