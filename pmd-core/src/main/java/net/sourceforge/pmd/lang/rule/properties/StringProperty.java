/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports single String values.
 *
 * @author Brian Remedios
 */
public class StringProperty extends AbstractSingleValueProperty<String> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<String>(String.class) {

        @Override
        public StringProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            return new StringProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById), 0f);
        }
    };


    /**
     * Constructor for StringProperty.
     *
     * @param theName         String
     * @param theDescription  String
     * @param theDefaultValue String
     * @param theUIOrder      float
     */
    public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
        super(theName, theDescription, theDefaultValue, theUIOrder);
    }


    @Override
    public Class<String> type() {
        return String.class;
    }


    @Override
    public String createFrom(String valueString) {
        return valueString;
    }
}
