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
public class CharacterProperty extends AbstractProperty<Character> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<CharacterProperty>(
            Character.class) {

        public CharacterProperty createWith(Map<String, String> valuesById) {
            return new CharacterProperty(nameIn(valuesById), descriptionIn(valuesById),
                    defaultValueIn(valuesById) != null ? new Character(defaultValueIn(valuesById).charAt(0)) : null, 0f);
        }
    };

    /**
     * Constructor for CharacterProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefault Character
     * @param theUIOrder float
     */
    public CharacterProperty(String theName, String theDescription, Character theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
    }

    /**
     * Constructor for CharacterProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultStr String
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public CharacterProperty(String theName, String theDescription, String defaultStr, float theUIOrder) {
        this(theName, theDescription, charFrom(defaultStr), theUIOrder);
    }

    /**
     * @param charStr String
     * @return Character
     */
    public static Character charFrom(String charStr) {

        if (charStr == null || charStr.length() != 1) {
            throw new IllegalArgumentException("missing/invalid character value");
        }
        return charStr.charAt(0);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Character> type() {
        return Character.class;
    }

    /**
     * @param valueString String
     * @return Object
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Character valueFrom(String valueString) throws IllegalArgumentException {
        return charFrom(valueString);
    }

    /**
     * Method defaultAsString.
     * 
     * @return String
     */
    protected String defaultAsString() {
        return Character.toString(defaultValue());
    }
}
