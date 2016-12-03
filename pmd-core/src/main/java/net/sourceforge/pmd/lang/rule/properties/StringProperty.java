/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports single String values.
 *
 * @author Brian Remedios
 */
public class StringProperty extends AbstractProperty<String> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<StringProperty>(
            String.class) {

        @Override
        public StringProperty createWith(Map<String, String> valuesById) {
            return new StringProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById), 0f);
        }
    };

    /**
     * Constructor for StringProperty.
     *
     * @param theName
     *            String
     * @param theDescription
     *            String
     * @param theDefaultValue
     *            String
     * @param theUIOrder
     *            float
     */
    public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
        super(theName, theDescription, theDefaultValue, theUIOrder);
    }

    /**
     * @return String
     */
    @Override
    protected String defaultAsString() {
        return defaultValue();
    }

    /**
     *
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    @Override
    public Class<String> type() {
        return String.class;
    }

    /**
     *
     * @param valueString
     *            String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    @Override
    public String valueFrom(String valueString) {
        return valueString;
    }
}
