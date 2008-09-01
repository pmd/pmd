/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that support double-type property values.
 * 
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractNumericProperty<Double> {

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min double
	 * @param max double
	 * @param theDefault double
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);
		
		isMultiValue(false);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Double> type() {
		return Double.class;
	}

	/**
	 * Deserializes a string into its Double form.
	 * 
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}
}
