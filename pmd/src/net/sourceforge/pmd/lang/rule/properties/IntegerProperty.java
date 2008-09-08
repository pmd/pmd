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
	 * Constructor for IntegerProperty.
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
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Integer> type() {
		return Integer.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Integer.valueOf(value);
	}
}
