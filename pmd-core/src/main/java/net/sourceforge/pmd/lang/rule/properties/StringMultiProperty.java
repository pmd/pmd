/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;


import java.util.Arrays;
import java.util.List;
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
public class StringMultiProperty extends AbstractMultiValueProperty<String> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<List<String>>(String
                                                                                                              .class) {

        @Override
        public StringMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new StringMultiProperty(nameIn(valuesById), descriptionIn(valuesById),
                                           StringUtil.substringsOf(defaultValueIn(valuesById), delimiter), 0.0f, delimiter);
        }
    };

    /**
     * Constructor for StringProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theDefaults    String[]
     * @param theUIOrder     float
     * @param delimiter      String
     *
     * @throws IllegalArgumentException if a default value contains the delimiter
     * @throws NullPointerException     if the defaults array is null
     */
    public StringMultiProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder,
                               char delimiter) {
        this(theName, theDescription, Arrays.asList(theDefaults), theUIOrder, delimiter);
    }

    /**
     * Constructor for StringProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theDefaults    String[]
     * @param theUIOrder     float
     * @param delimiter      String
     *
     * @throws IllegalArgumentException if a default value contains the delimiter
     * @throws NullPointerException     if the defaults array is null
     */
    public StringMultiProperty(String theName, String theDescription, List<String> theDefaults, float theUIOrder,
                               char delimiter) {
        super(theName, theDescription, theDefaults, theUIOrder, delimiter);

        checkDefaults(theDefaults, delimiter);
    }


    /**
     * Checks if the values are valid.
     *
     * @param defaultValue The default value
     * @param delim        The delimiter
     *
     * @throws IllegalArgumentException if one value contains the delimiter
     */
    private static void checkDefaults(List<String> defaultValue, char delim) {

        if (defaultValue == null) {
            return;
        }

        for (String aDefaultValue : defaultValue) {
            if (aDefaultValue.indexOf(delim) >= 0) {
                throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
            }
        }
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public List<String> valueFrom(String valueString) {
        return Arrays.asList(StringUtil.substringsOf(valueString, multiValueDelimiter()));
    }


    /**
     * Returns true if the multi value delimiter is present in the string.
     *
     * @param value String
     *
     * @return boolean
     */
    private boolean containsDelimiter(String value) {
        return value.indexOf(multiValueDelimiter()) >= 0;
    }


    private String illegalCharMsg() {
        return "Value cannot contain the '" + multiValueDelimiter() + "' character";
    }


    @Override
    protected String valueErrorFor(String value) {

        if (value == null) {
            return "missing value";
        }

        if (containsDelimiter(value)) {
            return illegalCharMsg();
        }

        // TODO - eval against regex checkers

        return null;
    }

    @Override
    protected String createFrom(String toParse) {
        return toParse;
    }
}
