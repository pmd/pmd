package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that supports Character values.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class CharacterProperty extends AbstractPMDProperty {

	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault char
	 * @param theUIOrder float
	 */
	public CharacterProperty(String theName, String theDescription, char theDefault, float theUIOrder) {
		super(theName, theDescription, new Character(theDefault), theUIOrder);
	}

	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults char[]
	 * @param theUIOrder float
	 * @param delimiter char
	 */
	public CharacterProperty(String theName, String theDescription, char[] theDefaults, float theUIOrder, char delimiter) {
		this(theName, theDescription, asCharacters(theDefaults), theUIOrder, delimiter);
	}
	
	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults String
	 * @param theUIOrder float
	 * @param delimiter char
	 */
	public CharacterProperty(String theName, String theDescription, String theDefaults, float theUIOrder, char delimiter) {
		this(theName, theDescription, theDefaults.toCharArray(), theUIOrder, delimiter);
	}	
	
	/**
	 * Constructor for CharacterProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults char[]
	 * @param theUIOrder float
	 * @param delimiter char
	 */
	public CharacterProperty(String theName, String theDescription, Character[] theDefaults, float theUIOrder, char delimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		multiValueDelimiter(delimiter);
		maxValueCount(Integer.MAX_VALUE);
	}
	
	/**
	 * Method asCharacters.
	 * @param chars char[]
	 * @return Character[]
	 */
	private static final Character[] asCharacters(char[] chars) {
		Character[] characters = new Character[chars.length];
		for (int i=0; i<chars.length; i++) characters[i] = new Character(chars[i]);
		return characters;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Character.class;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String valueString) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) {
			if (valueString.length() > 1) throw new IllegalArgumentException(valueString);
			return new Character(valueString.charAt(0));
		}
		
		String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter);
		
		Character[] chars = new Character[values.length];
		for (int i=0; i<values.length; i++) chars[i] = new Character(values[i].charAt(0));
		return chars;
	}
}
