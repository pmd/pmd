/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype that supports multiple String values. Note that all
 * strings must be filtered by the delimiter character.
 * 
 * @author Brian Remedios
 */
public class StringMultiProperty extends AbstractProperty<String[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<StringMultiProperty>(
            String[].class) {

        public StringMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new StringMultiProperty(nameIn(valuesById), descriptionIn(valuesById), StringUtil.substringsOf(
                    defaultValueIn(valuesById), delimiter), 0.0f, delimiter);
        }
    };

    /**
     * Constructor for StringProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefaults String[]
     * @param theUIOrder float
     * @param delimiter String
     * @throws IllegalArgumentException
     */
    public StringMultiProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder,
            char delimiter) {
        super(theName, theDescription, theDefaults, theUIOrder, delimiter);

        checkDefaults(theDefaults, delimiter);
    }

    /**
     * @param defaultValue
     * @param delim
     * @throws IllegalArgumentException
     */
    private static void checkDefaults(String[] defaultValue, char delim) {

        if (defaultValue == null) {
            return;
        }

        for (int i = 0; i < defaultValue.length; i++) {
            if (defaultValue[i].indexOf(delim) >= 0) {
                throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
            }
        }
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<String[]> type() {
        return String[].class;
    }

    /**
     * @param valueString String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public String[] valueFrom(String valueString) {
        return StringUtil.substringsOf(valueString, multiValueDelimiter());
    }

    /**
     * @param value String
     * @return boolean
     */
    private boolean containsDelimiter(String value) {
        return value.indexOf(multiValueDelimiter()) >= 0;
    }

    /**
     * @return String
     */
    private final String illegalCharMsg() {
        return "Value cannot contain the '" + multiValueDelimiter() + "' character";
    }

    /**
     * 
     * @param value Object
     * @return String
     */
    protected String valueErrorFor(Object value) {

        if (value == null) {
            return "missing value";
        }

        String testValue = (String) value;
        if (containsDelimiter(testValue)) {
            return illegalCharMsg();
        }

        // TODO - eval against regex checkers

        return null;
    }

    /**
     * @return boolean
     * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
     */
    @Override
    public boolean isMultiValue() {
        return true;
    }
}
