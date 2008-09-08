/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that supports single Character values.
 * 
 * @author Brian Remedios
 */
public class CharacterProperty extends AbstractProperty<Character> {

	/**
	 * Constructor for CharacterProperty.
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
     * @param theName String
     * @param theDescription String
     * @param defaultStr String
     * @param theUIOrder float
     */
    public CharacterProperty(String theName, String theDescription, String defaultStr, float theUIOrder) {
        this(theName, theDescription, charFrom(defaultStr), theUIOrder);
    }
	
    public static Character charFrom(String charStr) {
        return charStr.charAt(0);
    }
    
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Character> type() {
		return Character.class;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Character valueFrom(String valueString) throws IllegalArgumentException {
			if (valueString.length() > 1) {
			    throw new IllegalArgumentException(valueString);
			}
			return Character.valueOf(valueString.charAt(0));
	}
}
