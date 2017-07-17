/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.ValueParser.BOOLEAN_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Defines a property type that supports single Boolean values.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class BooleanProperty extends AbstractSingleValueProperty<Boolean> {

    /** Factory. */
    public static final PropertyDescriptorFactory<Boolean> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<Boolean>(Boolean.class) {
            @Override
            public BooleanProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                return new BooleanProperty(nameIn(valuesById),
                                           descriptionIn(valuesById),
                                           BOOLEAN_PARSER.valueOf(defaultValueIn(valuesById)),
                                           0f,
                                           isDefinedExternally);
            }
        }; // @formatter:on


    /**
     * Constructor for BooleanProperty limited to a single value. Converts
     * default argument string into a boolean.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultBoolStr String representing the default value.
     * @param theUIOrder     UI order
     *
     * @deprecated will be removed in 7.0.0
     */
    public BooleanProperty(String theName, String theDescription, String defaultBoolStr, float theUIOrder) {
        this(theName, theDescription, Boolean.valueOf(defaultBoolStr), theUIOrder, false);
    }


    /** Master constructor. */
    private BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValue, theUIOrder, isDefinedExternally);
    }


    /**
     * Constructor.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValue   Default value
     * @param theUIOrder     UI order
     */
    public BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder) {
        this(theName, theDescription, defaultValue, theUIOrder, false);
    }


    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }


    @Override
    public Boolean createFrom(String propertyString) throws IllegalArgumentException {
        return BOOLEAN_PARSER.valueOf(propertyString);
    }
}
