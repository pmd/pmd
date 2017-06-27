/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.BOOLEAN_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports multiple Boolean values.
 *
 * @author Brian Remedios
 */
public final class BooleanMultiProperty extends AbstractMultiValueProperty<Boolean> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<List<Boolean>>(Boolean.class) {
            @Override
            public BooleanMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                char delimiter = delimiterIn(valuesById);
                return new BooleanMultiProperty(nameIn(valuesById),
                                                descriptionIn(valuesById),
                                                parsePrimitives(defaultValueIn(valuesById), delimiter, BOOLEAN_PARSER),
                                                0f);
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
        this(theName, theDescription, Arrays.asList(defaultValues), theUIOrder);
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
        super(theName, theDescription, defaultValues, theUIOrder);
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
