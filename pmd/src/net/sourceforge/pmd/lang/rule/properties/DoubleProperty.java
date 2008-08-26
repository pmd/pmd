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
	 * @param min double
	 * @param max double
	 * @param theDefault double
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public DoubleProperty(String theName, String theDescription, double min, double max, double theDefault, float theUIOrder) {
		super(theName, theDescription, Double.valueOf(min), Double.valueOf(max), Double.valueOf(theDefault), theUIOrder);
		
		isMultiValue(false);
	}

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min double
	 * @param max double
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public DoubleProperty(String theName, String theDescription, double min, double max, double[] defaultValues, float theUIOrder) {
		this(theName, theDescription, Double.valueOf(min), Double.valueOf(max), asDoubles(defaultValues), theUIOrder);		
	}
	
	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min Double
	 * @param max Double
	 * @param defaultValues Double[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
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
	 * Returns the primitive doubles in their wrapped form.
	 * 
	 * @param d double[]
	 * @return Double[]
	 */
	private static final Double[] asDoubles(double[] d) {
		Double[] doubles = new Double[d.length];
		for (int i=0; i<d.length; i++) {
		    doubles[i] = Double.valueOf(d[i]);
		}
		return doubles;
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

	/**
	 * Creates and returns an array of the specified size for the
	 * the Double type this class is responsible for.
	 * 
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
