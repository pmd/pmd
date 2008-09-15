/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype that supports multiple Long property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class LongMultiProperty extends AbstractMultiNumericProperty<Long[]> {

	/**
	 * Constructor for LongProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min Long
	 * @param max Long
	 * @param theDefaults Long[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public LongMultiProperty(String theName, String theDescription, Long min, Long max, Long[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Long[]> type() {
		return Long[].class;
	}
	
	/**
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Long.valueOf(value);
	}

	/**
	 * Returns an array of the correct type for the receiver.
	 * 
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Long[size];
	}
}
