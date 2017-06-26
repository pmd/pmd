/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.CHARACTER_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports single Character values.
 *
 * @author Brian Remedios
 */
public class CharacterProperty extends AbstractSingleValueProperty<Character> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<Character>(Character.class) {

        @Override
        public CharacterProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            return new CharacterProperty(nameIn(valuesById),
                                         descriptionIn(valuesById),
                                         CHARACTER_PARSER.valueOf(defaultValueIn(valuesById)),
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
        return CHARACTER_PARSER.valueOf(charStr);
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
