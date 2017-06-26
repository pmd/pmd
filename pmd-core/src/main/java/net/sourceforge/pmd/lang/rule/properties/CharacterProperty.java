/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports single Character values.
 *
 * @author Brian Remedios
 */
public class CharacterProperty extends AbstractSingleValueProperty<Character> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<Character>(Character.class) {

        @Override
        public CharacterProperty createWith(Map<String, String> valuesById) {
            return new CharacterProperty(nameIn(valuesById),
                                         descriptionIn(valuesById),
                                         defaultValueIn(valuesById) == null ? null
                                                                            : defaultValueIn(valuesById).charAt(0),
                                         0f);
        }
    };

    /**
     * Constructor for CharacterProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theDefault     Character
     * @param theUIOrder     float
     */
    public CharacterProperty(String theName, String theDescription, Character theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
    }

    /**
     * Constructor for CharacterProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param defaultStr     String
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public CharacterProperty(String theName, String theDescription, String defaultStr, float theUIOrder) {
        this(theName, theDescription, charFrom(defaultStr), theUIOrder);
    }


    /**
     * Parses a String into a Character.
     *
     * @param charStr String to parse
     *
     * @return Parsed Character
     *
     * @throws IllegalArgumentException if the String doesn't have length 1
     */
    public static Character charFrom(String charStr) {
        if (charStr == null || charStr.length() != 1) {
            throw new IllegalArgumentException("missing/invalid character value");
        }
        return charStr.charAt(0);
    }

    @Override
    public Class<Character> type() {
        return Character.class;
    }


    @Override
    public Character createFrom(String valueString) throws IllegalArgumentException {
        return charFrom(valueString);
    }

}
