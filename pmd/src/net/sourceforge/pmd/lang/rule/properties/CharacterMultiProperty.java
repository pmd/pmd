/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that supports multiple Character values.
 * 
 * @author Brian Remedios
 */
public class CharacterMultiProperty extends AbstractDelimitedProperty<Character[]> {
	
	public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<CharacterMultiProperty>(Character[].class) {

		public CharacterMultiProperty createWith(Map<String, String> valuesById) {
			return new CharacterMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					null
					);
		}
	};
	
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
		super(theName, theDescription, theDefaults, delimiter, theUIOrder);
		
		if (theDefaults != null) {
			for (int i=0; i<theDefaults.length; i++) {
				if (theDefaults[i].charValue() == delimiter) {
					throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
				}
			}
		}
	}
	
	/**
	 * Constructor for CharacterProperty that accepts additional params from a map.
	 * 
	 * @param theName
	 * @param theDescription
	 * @param theDefaults
	 * @param otherParams
	 */
	public CharacterMultiProperty(String theName, String theDescription, String theDefaults, Map<String, String> otherParams) {
	    this(theName, theDescription, charsIn(theDefaults, delimiterIn(otherParams)), 0.0f, delimiterIn(otherParams));
	}
	
	private static Character[] charsIn(String charString, char delimiter) {
	    
	    String[] values = StringUtil.substringsOf(charString, delimiter);
	    Character[] chars = new Character[values.length];
	    
	    for (int i=0; i<values.length;i++) {
	        if (values.length != 1) {
	            throw new IllegalArgumentException("missing/ambiguous character value");
	        }
	        chars[i] = values[i].charAt(0);
	    }
	    return chars;
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Character[]> type() {
		return Character[].class;
	}
		
	/**
	 * @param valueString String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Character[] valueFrom(String valueString) throws IllegalArgumentException {
		String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter());
		
		Character[] chars = new Character[values.length];
		for (int i=0; i<values.length; i++) {
		    chars[i] = Character.valueOf(values[i].charAt(0));
		}
		return chars;
	}
}
