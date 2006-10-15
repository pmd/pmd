package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype that supports String values.
 * When capturing multiple values, all strings must be filtered by the delimiter character.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class StringProperty extends AbstractPMDProperty {
		
	private int preferredRowCount;
	
	public static final char defaultDelimiter = '|';
	
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaultValue String
	 * @param theUIOrder float
	 */
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		this(theName, theDescription, theDefaultValue, theUIOrder, defaultDelimiter);
		
		maxValueCount(1);
	}
		
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theValues String[]
	 * @param theUIOrder float
	 * @param aMultiValueDelimiter String
	 */
	public StringProperty(String theName, String theDescription, String[] theValues, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theValues, theUIOrder);
		
		maxValueCount(Integer.MAX_VALUE);
		multiValueDelimiter(aMultiValueDelimiter);
	}
	
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaultValue Object
	 * @param theUIOrder float
	 * @param aMultiValueDelimiter String
	 */
	protected StringProperty(String theName, String theDescription, Object theDefaultValue, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
		
		maxValueCount(Integer.MAX_VALUE);
		multiValueDelimiter(aMultiValueDelimiter);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return String.class;
	}
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String valueString) {
		
		if (maxValueCount() == 1) return valueString;
		
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
		return "Value cannot contain the \"" + multiValueDelimiter + "\" character";
	}
	
	/**
	 * 
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {

		if (maxValueCount() == 1) {
			String testValue = (String)value;
			if (!containsDelimiter(testValue)) return null;			
			return illegalCharMsg();
		}
		
		String[] values = (String[])value;
		for (int i=0; i<values.length; i++) {
			if (!containsDelimiter(values[i])) continue;	
			return illegalCharMsg();
			}
		
		return null;
	}
	
	public int preferredRowCount() {
		return preferredRowCount;
	}
}
