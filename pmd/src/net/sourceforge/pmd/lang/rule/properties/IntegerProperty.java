/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype that supports single Integer property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class IntegerProperty extends AbstractNumericProperty<Integer> {

	/**
	 * Constructor for IntegerProperty that limits itself to a single value within the specified limits. 
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param min Integer
	 * @param max Integer
	 * @param theDefault Integer
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	/**
	 * Constructor for IntegerProperty that limits itself to a single value within the specified limits. 
	 * Converts string arguments into the Float values.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param minStr String
	 * @param maxStr String
	 * @param defaultStr String
	 * @param theUIOrder
	 * @throws IllegalArgumentException
	 */
	public IntegerProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, intFrom(minStr), intFrom(maxStr), intFrom(defaultStr), theUIOrder);       
    }
	
	/**
	 * @param numberString String
	 * @return Integer
	 */
	public static Integer intFrom(String numberString) {
	    return Integer.valueOf(numberString);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Integer> type() {
		return Integer.class;
	}

	/**
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return intFrom(value);
	}
}
