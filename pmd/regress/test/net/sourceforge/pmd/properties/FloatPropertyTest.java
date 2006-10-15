package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.FloatProperty;

/**
 */
public class FloatPropertyTest extends AbstractPropertyDescriptorTester {

	public FloatPropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return new Float((int)(System.currentTimeMillis() % 100));
		
		Float[] values = new Float[count];
		for (int i=0; i<values.length; i++) values[i] = (Float)createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		
		return maxCount == 1 ?
				new FloatProperty("testFloat", "Test float property", 9.0f, 1.0f) :
				new FloatProperty("testFloat", "Test float property", new float[] {-1,0,1,2}, 1.0f, maxCount);
		}

}
