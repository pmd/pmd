package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.FloatMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;

/**
 * Evaluates the functionality of the FloatProperty descriptor by testing its ability to catch creation
 * errors (illegal args), flag out-of-range test values, and serialize/deserialize groups of float values
 * onto/from a string buffer.
 * 
 * @author Brian Remedios
 */
public class FloatPropertyTest extends AbstractPropertyDescriptorTester {

	private static final float MIN = 1.0f;
	private static final float MAX = 11.0f;
	private static final float SHIFT = 3.0f;
	
	public static FloatProperty randomProperty(int nameLength, int descLength, boolean multiValue) {
		
		float defalt = randomFloat(0, 1000f);
		
		return new FloatProperty(
			randomString(nameLength), randomString(descLength),
			defalt - 1000f, defalt + 1000, defalt, 0f
			);
	}
	
	public FloatPropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return Float.valueOf(randomFloat(MIN, MAX));
		
		Float[] values = new Float[count];
		for (int i=0; i<values.length; i++) values[i] = (Float)createValue(1);
		return values;
	}

	/**
	 * Creates and returns (count) number of out-of-range float values
	 * 
	 * @param count int
	 * @return Object
	 */
	protected Object createBadValue(int count) {
		
		if (count == 1) return Float.valueOf(
				randomBool() ?
						randomFloat(MIN - SHIFT, MIN) :
						randomFloat(MAX, MAX + SHIFT)
						);
		
		Float[] values = new Float[count];
		for (int i=0; i<values.length; i++) values[i] = (Float)createBadValue(1);
		return values;
	}	
	
	/**
	 * Method createProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(boolean multiValue) {
		
		return multiValue ?
			new FloatMultiProperty("testFloat", "Test float property", MIN, MAX, new Float[] {-1f,0f,1f,2f}, 1.0f) :
			new FloatProperty("testFloat", "Test float property", MIN, MAX, 9.0f, 1.0f) ;					
		}

	/**
	 * Method createBadProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new FloatMultiProperty("testFloat", "Test float property", 0f, 5f, new Float[] {-1f,0f,1f,2f}, 1.0f) :
			new FloatProperty("testFloat", "Test float property", 5f, 4f, 9.0f, 1.0f) ;
		}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FloatPropertyTest.class);
    }
}
