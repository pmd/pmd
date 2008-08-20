/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

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
		
		isMultiValue(false);
	}

	/**
	 * Constructor for FloatProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 */
	public FloatProperty(String theName, String theDescription, float[] defaultValues, float theUIOrder) {
		this(theName, theDescription, asFloats(defaultValues), theUIOrder);		
	}
	
	/**
	 * Constructor for FloatProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Float[]
	 * @param theUIOrder float
	 */
	public FloatProperty(String theName, String theDescription, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Float> type() {
		return Float.class;
	}

	/**
	 * Method asFloats.
	 * @param f float[]
	 * @return Float[]
	 */
	private static final Float[] asFloats(float[] f) {
		Float[] floats = new Float[f.length];
		for (int i=0; i<f.length; i++) {
		    floats[i] = new Float(f[i]);
		}
		return floats;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Float.valueOf(value);
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
