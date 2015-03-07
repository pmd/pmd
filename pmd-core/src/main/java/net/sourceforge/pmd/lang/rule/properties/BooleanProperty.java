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
public class BooleanProperty extends AbstractScalarProperty<Boolean> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<BooleanProperty>(
            Boolean.class) {

        public BooleanProperty createWith(Map<String, String> valuesById) {
            return new BooleanProperty(nameIn(valuesById), descriptionIn(valuesById),
                    Boolean.valueOf(defaultValueIn(valuesById)), 0f);
        }
    };

    /**
     * Constructor for BooleanProperty limited to a single value.
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultValue boolean
     * @param theUIOrder float
     */
    public BooleanProperty(String theName, String theDescription, Boolean defaultValue, float theUIOrder) {
        super(theName, theDescription, Boolean.valueOf(defaultValue), theUIOrder);
    }

    /**
     * Constructor for BooleanProperty limited to a single value. Converts
     * default argument string into a boolean.
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultBoolStr String
     * @param theUIOrder float
     */
    public BooleanProperty(String theName, String theDescription, String defaultBoolStr, float theUIOrder) {
        this(theName, theDescription, Boolean.valueOf(defaultBoolStr), theUIOrder);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Boolean> type() {
        return Boolean.class;
    }

    /**
     * @return String
     */
    protected String defaultAsString() {
        return Boolean.toString(defaultValue());
    }

    /**
     * Creates and returns a Boolean instance from a raw string
     * 
     * @param value String
     * @return Object
     */
    protected Object createFrom(String value) {
        return Boolean.valueOf(value);
    }
}
