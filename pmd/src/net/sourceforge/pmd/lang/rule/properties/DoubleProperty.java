/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that support double-type property values.
 * 
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractNumericProperty {

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault double
	 * @param theUIOrder float
	 */
	public DoubleProperty(String theName, String theDescription, double min, double max, double theDefault, float theUIOrder) {
		super(theName, theDescription, new Double(min), new Double(max), new Double(theDefault), theUIOrder);
		
		isMultiValue(false);
	}

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 */
	public DoubleProperty(String theName, String theDescription, double min, double max, double[] defaultValues, float theUIOrder) {
		this(theName, theDescription, new Double(min), new Double(max), asDoubles(defaultValues), theUIOrder);		
	}
	
	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Double[]
	 * @param theUIOrder float
	 */
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
		
		isMultiValue(true);
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
	 * Method asDoubles.
	 * @param d double[]
	 * @return Double[]
	 */
	private static final Double[] asDoubles(double[] d) {
		Double[] doubles = new Double[d.length];
		for (int i=0; i<d.length; i++) {
		    doubles[i] = new Double(d[i]);
		}
		return doubles;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
