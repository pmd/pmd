package net.sourceforge.pmd.properties;


/**
 * Defines a property type that supports Boolean values.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class BooleanProperty extends AbstractScalarProperty {

	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValue boolean
	 * @param theUIOrder float
	 */
	public BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder) {
		super(theName, theDescription, Boolean.valueOf(defaultValue), theUIOrder);
	}

	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public BooleanProperty(String theName, String theDescription, boolean[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asBooleans(defaultValues), theUIOrder, theMaxValues);
		
	}
	
	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Boolean[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public BooleanProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
	}
	
	/**
	 * Method asBooleans.
	 * @param bools boolean[]
	 * @return Boolean[]
	 */
	private static final Boolean[] asBooleans(boolean[] bools) {
		Boolean[] booleans = new Boolean[bools.length];
		for (int i=0; i<bools.length; i++) booleans[i] = Boolean.valueOf(bools[i]);
		return booleans;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Boolean.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Boolean.valueOf(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Boolean[size];
	}
}
