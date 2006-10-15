package net.sourceforge.pmd.properties;


/**
 * Defines a datatype that supports the Integer property values.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class IntegerProperty extends AbstractScalarProperty {

	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault int
	 * @param theUIOrder float
	 */
	public IntegerProperty(String theName, String theDescription, int theDefault, float theUIOrder) {
		super(theName, theDescription, new Integer(theDefault), theUIOrder);
	}

	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults int[]
	 * @param theUIOrder float
	 * @param maxCount int
	 */
	public IntegerProperty(String theName, String theDescription, int[] theDefaults, float theUIOrder, int maxCount) {
		this(theName, theDescription, asIntegers(theDefaults), theUIOrder, maxCount);
	}
	
	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults Integer[]
	 * @param theUIOrder float
	 * @param maxCount int
	 */
	public IntegerProperty(String theName, String theDescription, Integer[] theDefaults, float theUIOrder, int maxCount) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		maxValueCount(maxCount);
	}
	
	/**
	 * Method asIntegers.
	 * @param ints int[]
	 * @return Integer[]
	 */
	private static final Integer[] asIntegers(int[] ints) {
		Integer[] integers = new Integer[ints.length];
		for (int i=0; i<ints.length; i++) integers[i] = new Integer(ints[i]);
		return integers;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Integer.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return new Integer(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Integer[size];
	}
}
