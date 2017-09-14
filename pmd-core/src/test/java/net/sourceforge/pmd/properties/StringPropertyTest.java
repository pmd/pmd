/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

/**
 * Evaluates the functionality of the StringProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag invalid strings per any
 * specified expressions, and serialize/deserialize groups of strings onto/from
 * a string buffer.
 *
 * @author Brian Remedios
 */
public class StringPropertyTest extends AbstractPropertyDescriptorTester<String> {

    private static final int MAX_STRING_LENGTH = 52;
    private static final char DELIMITER = '|';
    private static final char[] CHARSET = filter(ALL_CHARS.toCharArray(), DELIMITER);


    public StringPropertyTest() {
        super("String");
    }


    @Override
    protected String createValue() {
        return newString();
    }


    /**
     * Method newString.
     *
     * @return String
     */
    private String newString() {

        int strLength = randomInt(1, MAX_STRING_LENGTH);

        char[] chars = new char[strLength];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = randomCharIn(CHARSET);
        }
        return new String(chars);
    }


    /**
     * Method randomCharIn.
     *
     * @param chars char[]
     *
     * @return char
     */
    private char randomCharIn(char[] chars) {
        return randomChar(chars);
    }


    @Override
    protected String createBadValue() {
        return null;
    }


    @Override
    protected PropertyDescriptor<String> createProperty() {
        return new StringProperty("testString", "Test string property", "brian", 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<String>> createMultiProperty() {
        return new StringMultiProperty("testString", "Test string property",
                                       new String[] {"hello", "world"}, 1.0f, DELIMITER);
    }


    @Override
    protected PropertyDescriptor<String> createBadProperty() {
        return new StringProperty("", "Test string property", "brian", 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<String>> createBadMultiProperty() {
        return new StringMultiProperty("testString", "Test string property",
                                       new String[] {"hello", "world", "a" + DELIMITER + "b"}, 1.0f, DELIMITER);
    }
}
