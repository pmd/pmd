/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype that supports String values.
 * 
 * When capturing multiple values, all strings must be filtered to eliminate
 * occurrences of the delimiter character.
 * 
 * @author Brian Remedios
 */
public class StringProperty extends AbstractProperty {
		
	private int preferredRowCount;
	
	public static final char DEFAULT_DELIMITER = '|';
	
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaultValue String
	 * @param theUIOrder float
	 */
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		this(theName, theDescription, theDefaultValue, theUIOrder, DEFAULT_DELIMITER);
	}
		
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults String[]
	 * @param theUIOrder float
	 * @param aMultiValueDelimiter String
	 * @throws IllegalArgumentException
	 */
	public StringProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);
			
		isMultiValue(true);
		multiValueDelimiter(aMultiValueDelimiter);

		checkDefaults(theDefaults, aMultiValueDelimiter);
	}
	
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaultValue Object
	 * @param theUIOrder float
	 * @param aMultiValueDelimiter String
	 * @throws IllegalArgumentException
	 */
	protected StringProperty(String theName, String theDescription, Object theDefaultValue, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
				
		isMultiValue(isArray(theDefaultValue));
		multiValueDelimiter(aMultiValueDelimiter);
		
		checkDefaults(theDefaultValue, aMultiValueDelimiter);
	}
	
	/**
	 * 
	 * @param defaultValue
	 * @param delim
	 * @throws IllegalArgumentException
	 */
	private static void checkDefaults(Object defaultValue, char delim) {
		
		if (defaultValue == null) { return;	}
		
		if (isArray(defaultValue) && defaultValue instanceof String[]) {
			String[] defaults = (String[])defaultValue;
			for (int i=0; i<defaults.length; i++) {
				if (defaults[i].indexOf(delim) >= 0) {
					throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
				}
			}
		}
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<?> type() {
		return String.class;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String valueString) {
		
		if (isMultiValue()) {
		    return StringUtil.substringsOf(valueString, multiValueDelimiter);
			}
		
		return valueString;
	}
	
	/**
	 * Method containsDelimiter.
	 * @param value String
	 * @return boolean
	 */
	private boolean containsDelimiter(String value) {
		return value.indexOf(multiValueDelimiter) >= 0;
	}
	
	/**
	 * Method illegalCharMsg.
	 * @return String
	 */
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
		if (isMultiValue() && containsDelimiter(testValue)) {
		    return illegalCharMsg();			
		}
		
		// TODO - eval against regex checkers
		
		return null;		
	}
	
	/**
	 * Method preferredRowCount.
	 * @return int
	 * @see net.sourceforge.pmd.PropertyDescriptor#preferredRowCount()
	 */
	public int preferredRowCount() {
		return preferredRowCount;
	}
}
