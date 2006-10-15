package net.sourceforge.pmd.properties;


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
	public Class type() {
		return Double.class;
	}

	/**
	 * Method asDoubles.
	 * @param doubles double[]
	 * @return Double[]
	 */
	private static final Double[] asDoubles(double[] doubles) {
		Double[] Doubles = new Double[doubles.length];
		for (int i=0; i<doubles.length; i++) Doubles[i] = new Double(doubles[i]);
		return Doubles;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return new Double(value);
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
