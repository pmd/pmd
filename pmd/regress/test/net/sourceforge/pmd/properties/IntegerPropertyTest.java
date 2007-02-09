package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 */
public class IntegerPropertyTest extends AbstractPropertyDescriptorTester {

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return new Integer((int)(System.currentTimeMillis() % 100));
		
		Integer[] values = new Integer[count];
		for (int i=0; i<values.length; i++) values[i] = (Integer)createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		
		return maxCount == 1 ?
				new IntegerProperty("testInteger", "Test integer property", 9, 1.0f) :
				new IntegerProperty("testInteger", "Test integer property", new int[] {-1,0,1,2}, 1.0f, maxCount);
		}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(IntegerPropertyTest.class);
    }
}
