/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that supports multiple double-type property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class DoubleMultiProperty extends AbstractMultiNumericProperty<Double[]> {
	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min Double
	 * @param max Double
	 * @param defaultValues Double[]
	 * @param theUIOrder float
	 */
	public DoubleMultiProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Double[]> type() {
		return Double[].class;
	}

	/**
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	/**
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
