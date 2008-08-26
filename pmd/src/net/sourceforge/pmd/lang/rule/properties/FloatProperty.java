/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a property type that support float property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class FloatProperty extends AbstractNumericProperty {

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
	public FloatProperty(String theName, String theDescription,	float min, float max, float theDefault, float theUIOrder) {
		super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);
		
		isMultiValue(false);
	}

	/**
	 * Constructor for FloatProperty that configures it to accept multiple values and any number of defaults.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param min float
	 * @param max float
	 * @param defaultValues float[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public FloatProperty(String theName, String theDescription, float min, float max, float[] defaultValues, float theUIOrder) {
		this(theName, theDescription, Float.valueOf(min), Float.valueOf(max), asFloats(defaultValues), theUIOrder);		
	}
	
	/**
	 * Constructor for FloatProperty that configures it to accept multiple values and any number of defaults.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param min Float
	 * @param max Float
	 * @param defaultValues Float[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public FloatProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Float> type() {
		return Float.class;
	}

	/**
	 * Converts an array of primitive float values into their wrapped equivalents.
	 * 
	 * @param f float[]
	 * @return Float[]
	 */
	private static final Float[] asFloats(float[] f) {
		Float[] floats = new Float[f.length];
		for (int i=0; i<f.length; i++) {
		    floats[i] = Float.valueOf(f[i]);
		}
		return floats;
	}

	/**
     * Creates and returns an array of the specified size for the
	 * the Float type this class is responsible for.
	 *
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	/**
	 * Returns an array of the correct type for the receiver.
	 * 
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
