/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that supports Character values.
 * 
 * @author Brian Remedios
 */
public class CharacterMultiProperty extends AbstractProperty<Character[]> {
	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults char[]
	 * @param theUIOrder float
	 * @param delimiter char
	 * @throws IllegalArgumentException
	 */
	public CharacterMultiProperty(String theName, String theDescription, Character[] theDefaults, float theUIOrder, char delimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		if (theDefaults != null) {
			for (int i=0; i<theDefaults.length; i++) {
				if (theDefaults[i].charValue() == delimiter) {
					throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
				}
			}
		}
		
		multiValueDelimiter(delimiter);
		isMultiValue(true);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Character[]> type() {
		return Character[].class;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Character[] valueFrom(String valueString) throws IllegalArgumentException {
		String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter);
		
		Character[] chars = new Character[values.length];
		for (int i=0; i<values.length; i++) {
		    chars[i] = Character.valueOf(values[i].charAt(0));
		}
		return chars;
	}
}
