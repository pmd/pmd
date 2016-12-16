/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/**
 * Evaluates the functionality of the StringProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag invalid strings per any
 * specified expressions, and serialize/deserialize groups of strings onto/from
 * a string buffer.
 *
 * @author Brian Remedios
 */
public class StringPropertyTest extends AbstractPropertyDescriptorTester {

    private static final int MAX_STRING_LENGTH = 52;
    private static final char DELIMITER = '|';
    private static final char[] CHARSET = filter(ALL_CHARS.toCharArray(), DELIMITER);

    public StringPropertyTest() {
        super("String");
    }

    /**
     * Method createValue.
     *
     * @param count
     *            int
     * @return Object
     */
    @Override
    protected Object createValue(int count) {

        if (count == 1) {
            return newString();
        }

        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = (String) createValue(1);
        }
        return values;
    }

    /**
     * Method createBadValue.
     *
     * @param count
     *            int
     * @return Object
     */
    @Override
    protected Object createBadValue(int count) {

        if (count == 1) {
            return null;
        }

        Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            values[i] = createBadValue(1);
        }
        return values;
    }

    /**
     * Method newString.
     *
     * @return String
     */
    private String newString() {

        int strLength = randomInt(0, MAX_STRING_LENGTH);

        char[] chars = new char[strLength];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = randomCharIn(CHARSET);
        }
        return new String(chars);
    }

    /**
     * Method randomCharIn.
     *
     * @param chars
     *            char[]
     * @return char
     */
    private char randomCharIn(char[] chars) {
        return randomChar(chars);
    }

    /**
     * Method createProperty.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createProperty(boolean multiValue) {
        return multiValue ? new StringMultiProperty("testString", "Test string property",
                new String[] { "hello", "world" }, 1.0f, DELIMITER)
                : new StringProperty("testString", "Test string property", "brian", 1.0f);
    }

    /**
     * Method createBadProperty.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createBadProperty(boolean multiValue) {
        return multiValue
                ? new StringMultiProperty("testString", "Test string property",
                        new String[] { "hello", "world", "a" + DELIMITER + "b" }, 1.0f, DELIMITER)
                : new StringProperty("", "Test string property", "brian", 1.0f);
    }
}
