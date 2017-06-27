/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.BOOLEAN_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports single Boolean values.
 *
 * @author Brian Remedios
 */
public class BooleanProperty extends AbstractSingleValueProperty<Boolean> {

    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<Boolean>(Boolean.class) {
            @Override
            public BooleanProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                return new BooleanProperty(nameIn(valuesById),
                                           descriptionIn(valuesById),
                                           BOOLEAN_PARSER.valueOf(defaultValueIn(valuesById)),
                                           0f);
            }
        }; // @formatter:on


    /**
     * Constructor.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValue   Default value
     * @param theUIOrder     UI order
     */
    public BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder) {
        super(theName, theDescription, defaultValue, theUIOrder);
    }


    /**
     * Constructor for BooleanProperty limited to a single value. Converts
     * default argument string into a boolean.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultBoolStr String representing the default value.
     * @param theUIOrder     UI order
     *
     * @deprecated ?
     */
    public BooleanProperty(String theName, String theDescription, String defaultBoolStr, float theUIOrder) {
        this(theName, theDescription, Boolean.valueOf(defaultBoolStr), theUIOrder);
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
