package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;

/**
 * 
 * @author Brian Remedios
 */
public class FloatPropertyTest extends AbstractPropertyDescriptorTester {

	private static final float MIN = 1.0f;
	private static final float MAX = 11.0f;
	private static final float SHIFT = 3.0f;
	
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
			new FloatProperty("testFloat", "Test float property", MIN, MAX, new float[] {-1,0,1,2}, 1.0f) :
			new FloatProperty("testFloat", "Test float property", MIN, MAX, 9.0f, 1.0f) ;					
		}

	/**
	 * Method createBadProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new FloatProperty("testFloat", "Test float property", 0f, 5f, new float[] {-1,0,1,2}, 1.0f) :
			new FloatProperty("testFloat", "Test float property", 5f, 4f, 9.0f, 1.0f) ;
		}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FloatPropertyTest.class);
    }
}
