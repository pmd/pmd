/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a property type that support float property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class FloatMultiProperty extends AbstractNumericProperty<Float[]> {
	/**
	 * Constructor for FloatProperty that configures it to accept multiple values and any number of defaults.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param min Float
	 * @param max Float
	 * @param defaultValues Float[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public FloatMultiProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Float[]> type() {
		return Float[].class;
	}

	/**
	 * Creates an property value of the right type from a raw string.
	 * 
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	/**
	 * Returns an array of the correct type for the receiver.
	 * 
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
