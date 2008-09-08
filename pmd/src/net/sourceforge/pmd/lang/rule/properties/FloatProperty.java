/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a property type that supports single float property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class FloatProperty extends AbstractNumericProperty<Float> {

	/**
	 * Constructor for FloatProperty that limits itself to a single value within the specified limits.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param min float
	 * @param max float
	 * @param theDefault float
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public FloatProperty(String theName, String theDescription,	Float min, Float max, Float theDefault, float theUIOrder) {
		super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);		
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Float> type() {
		return Float.class;
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
}
