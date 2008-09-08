/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype that supports the single Long property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class LongProperty extends AbstractNumericProperty<Long> {

	/**
	 * Constructor for LongProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min Long
	 * @param max Long
	 * @param theDefault Long
	 * @param theUIOrder float
	 */
	public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Long> type() {
		return Long.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Long.valueOf(value);
	}
}
