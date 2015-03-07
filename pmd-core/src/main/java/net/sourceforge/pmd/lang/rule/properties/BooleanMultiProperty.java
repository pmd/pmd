/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports multiple Boolean values.
 * 
 * @author Brian Remedios
 */
public class BooleanMultiProperty extends AbstractScalarProperty<Boolean[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<StringMultiProperty>(
            String[].class) {
        public BooleanMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new BooleanMultiProperty(nameIn(valuesById), descriptionIn(valuesById),
                    booleanValuesIn(defaultValueIn(valuesById), delimiter), 0f);
        }
    };

    /**
     * Constructor for BooleanMultiProperty that allows for multiple values.
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultValues Boolean[]
     * @param theUIOrder float
     */
    public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
        super(theName, theDescription, defaultValues, theUIOrder);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Boolean[]> type() {
        return Boolean[].class;
    }

    /**
     * @return boolean
     * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
     */
    @Override
    public boolean isMultiValue() {
        return true;
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

    /**
     * @param size int
     * @return Object[]
     */
    protected Boolean[] arrayFor(int size) {
        return new Boolean[size];
    }

    /**
     * @return String
     */
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
