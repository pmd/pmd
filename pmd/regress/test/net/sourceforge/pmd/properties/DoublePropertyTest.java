package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.DoubleProperty;

/**
 */
public class DoublePropertyTest extends AbstractPropertyDescriptorTester {

	public DoublePropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return new Double((int)(System.currentTimeMillis() % 100));
		
		Double[] values = new Double[count];
		for (int i=0; i<values.length; i++) values[i] = (Double)createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		
		return maxCount == 1 ?
			new DoubleProperty("testDouble", "Test double property", 9.0, 1.0f) :
			new DoubleProperty("testDouble", "Test double property", new double[] {-1,0,1,2}, 1.0f, maxCount);
		}

}
