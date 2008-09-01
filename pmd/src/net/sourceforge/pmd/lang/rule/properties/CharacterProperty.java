/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that supports Character values.
 * 
 * @author Brian Remedios
 */
public class CharacterProperty extends AbstractProperty<Character> {

	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault char
	 * @param theUIOrder float
	 */
	public CharacterProperty(String theName, String theDescription, Character theDefault, float theUIOrder) {
		super(theName, theDescription, Character.valueOf(theDefault), theUIOrder);
		
		isMultiValue(false);
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
