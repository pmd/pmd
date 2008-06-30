/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that support double property values.
 * 
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractScalarProperty {

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault double
	 * @param theUIOrder float
	 */
	public DoubleProperty(String theName, String theDescription, double theDefault, float theUIOrder) {
		super(theName, theDescription, new Double(theDefault), theUIOrder);
	}

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public DoubleProperty(String theName, String theDescription, double[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asDoubles(defaultValues), theUIOrder, theMaxValues);		
	}
	
	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Double[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public DoubleProperty(String theName, String theDescription, Double[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
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
