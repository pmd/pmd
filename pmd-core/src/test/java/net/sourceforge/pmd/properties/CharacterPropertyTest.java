/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import org.junit.Test;

/**
 * Evaluates the functionality of the CharacterProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid characters,
 * and serialize/deserialize any default values.
 *
 * @author Brian Remedios
 */
@Deprecated
public class CharacterPropertyTest extends AbstractPropertyDescriptorTester<Character> {

    private static final char DELIMITER = '|';
    private static final char[] CHARSET = filter(ALL_CHARS.toCharArray(), DELIMITER);


    public CharacterPropertyTest() {
        super("Character");
    }


    @Override
    @Test
    public void testErrorForBadSingle() {
    } // not until char properties use illegal chars


    @Override
    @Test
    public void testErrorForBadMulti() {
    } // not until char properties use illegal chars


    @Override
    protected Character createValue() {
        return randomChar(CHARSET);
    }


    @Override
    protected Character createBadValue() {
        return null;
    }


    @Override
    protected PropertyDescriptor<Character> createProperty() {
        return new CharacterProperty("testCharacter", "Test character property", 'a', 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Character>> createMultiProperty() {
        return new CharacterMultiProperty("testCharacter", "Test character property",
                                          new Character[] {'a', 'b', 'c'}, 1.0f, DELIMITER);
    }


    @Override
    protected PropertyDescriptor<Character> createBadProperty() {
        return new CharacterProperty("", "Test character property", 'a', 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Character>> createBadMultiProperty() {
        return new CharacterMultiProperty("testCharacter", "Test character property",
                                          new Character[] {'a', 'b', 'c'}, 1.0f, DELIMITER);
    }
}
