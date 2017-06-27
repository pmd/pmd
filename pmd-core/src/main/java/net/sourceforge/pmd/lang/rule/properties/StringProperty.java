/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Defines a datatype that supports single String values.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class StringProperty extends AbstractSingleValueProperty<String> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<String>(String.class) {
        @Override
        public StringProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            return new StringProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById), 0f);
        }
    };


    /**
     * Constructor.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValue   Default value
     * @param theUIOrder     UI order
     */
    public StringProperty(String theName, String theDescription, String defaultValue, float theUIOrder) {
        super(theName, theDescription, defaultValue, theUIOrder);
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
