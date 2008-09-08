/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype that supports multiple String values.
 * Note that all strings must be filtered by the delimiter character.
 * 
 * @author Brian Remedios
 */
public class StringMultiProperty extends AbstractProperty<String[]> {
		
	public static final char DEFAULT_DELIMITER = '|';
		
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults String[]
	 * @param theUIOrder float
	 * @param aMultiValueDelimiter String
	 * @throws IllegalArgumentException
	 */
	public StringMultiProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);

		multiValueDelimiter(aMultiValueDelimiter);

		checkDefaults(theDefaults, aMultiValueDelimiter);
	}
	
	/**
	 * 
	 * @param defaultValue
	 * @param delim
	 * @throws IllegalArgumentException
	 */
	private static void checkDefaults(String[] defaultValue, char delim) {
		
		if (defaultValue == null) { return;	}
		
		for (int i=0; i<defaultValue.length; i++) {
			if (defaultValue[i].indexOf(delim) >= 0) {
				throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
			}
		}
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<String[]> type() {
		return String[].class;
	}
	
	/**
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
	 */
	@Override
	public boolean isMultiValue() {
		return true;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public String[] valueFrom(String valueString) {
		    return StringUtil.substringsOf(valueString, multiValueDelimiter);
	}
	
	/**
	 * Method containsDelimiter.
	 * @param value String
	 * @return boolean
	 */
	private boolean containsDelimiter(String value) {
		return value.indexOf(multiValueDelimiter) >= 0;
	}
	
	private final String illegalCharMsg() {
		return "Value cannot contain the '" + multiValueDelimiter + "' character";
	}
	
	/**
	 * 
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {

		if (value==null) { return "missing value"; }
		
		String testValue = (String)value;
		if (containsDelimiter(testValue)) {
		    return illegalCharMsg();			
		}
		
		// TODO - eval against regex checkers
		
		return null;		
	}
}
