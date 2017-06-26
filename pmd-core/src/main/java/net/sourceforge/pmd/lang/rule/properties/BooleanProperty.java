/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports single Boolean values.
 *
 * @author Brian Remedios
 */
public class BooleanProperty extends AbstractSingleValueProperty<Boolean> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<Boolean>(
        Boolean.class) {

        @Override
        public BooleanProperty createWith(Map<String, String> valuesById) {
            return new BooleanProperty(nameIn(valuesById), descriptionIn(valuesById),
                                       Boolean.valueOf(defaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for BooleanProperty limited to a single value.
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
        return Boolean.valueOf(propertyString);
    }

}
