package net.sourceforge.pmd.properties;


/**
 * Defines a property type that support float property values.
 * 
 * @author Brian Remedios
 */
public class FloatProperty extends AbstractScalarProperty {

	/**
	 * Constructor for FloatProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault float
	 * @param theUIOrder float
	 */
	public FloatProperty(String theName, String theDescription,	float theDefault, float theUIOrder) {
		super(theName, theDescription, new Float(theDefault), theUIOrder);
	}

	/**
	 * Constructor for FloatProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public FloatProperty(String theName, String theDescription, float[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asFloats(defaultValues), theUIOrder, theMaxValues);		
	}
	
	/**
	 * Constructor for FloatProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Float[]
	 * @param theUIOrder float
	 * @param theMaxValues int
	 */
	public FloatProperty(String theName, String theDescription, Float[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Float.class;
	}

	/**
	 * Method asFloats.
	 * @param floats float[]
	 * @return Float[]
	 */
	private static final Float[] asFloats(float[] floats) {
		Float[] Floats = new Float[floats.length];
		for (int i=0; i<floats.length; i++) Floats[i] = new Float(floats[i]);
		return Floats;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return new Float(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
