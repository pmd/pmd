/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.CharacterMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;

/**
 * Evaluates the functionality of the CharacterProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid characters,
 * and serialize/deserialize any default values.
 *
 * @author Brian Remedios
 */
public class CharacterPropertyTest extends AbstractPropertyDescriptorTester {

    private static final char DELIMITER = '|';
    private static final char[] CHARSET = filter(ALL_CHARS.toCharArray(), DELIMITER);

    public CharacterPropertyTest() {
        super("Character");
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
            return new Character(randomChar(CHARSET));
        }

        Character[] values = new Character[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = (Character) createValue(1);
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

        Character[] values = new Character[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = (Character) createBadValue(1);
        }
        return values;
    }

    @Override
    @Test
    public void testErrorForBad() {
    } // not until char properties use illegal chars

    /**
     * Method createProperty.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createProperty(boolean multiValue) {

        return multiValue
                ? new CharacterMultiProperty("testCharacter", "Test character property",
                        new Character[] { 'a', 'b', 'c' }, 1.0f, DELIMITER)
                : new CharacterProperty("testCharacter", "Test character property", 'a', 1.0f);
    }

    /**
     * Creates a bad property that is missing either its name or description or
     * includes a delimiter in the set of legal values.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createBadProperty(boolean multiValue) {

        return multiValue
                ? new CharacterMultiProperty("testCharacter", "Test character property",
                        new Character[] { 'a', 'b', 'c' }, 1.0f, DELIMITER)
                : new CharacterProperty("", "Test character property", 'a', 1.0f);
    }
}
