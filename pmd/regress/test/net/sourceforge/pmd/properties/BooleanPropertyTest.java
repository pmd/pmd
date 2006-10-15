package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.BooleanProperty;

/**
 * @author Brian Remedios
 */
public class BooleanPropertyTest extends AbstractPropertyDescriptorTester {

	public BooleanPropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param valueCount int
	 * @return Object
	 */
	public Object createValue(int valueCount) {
		
		if (valueCount == 1) return System.currentTimeMillis() % 1 > 0 ?
			Boolean.TRUE : Boolean.FALSE;
		
		Boolean[] values = new Boolean[valueCount];
		for (int i=0; i<values.length; i++) values[i] = (Boolean)createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxValues int
	 * @return PropertyDescriptor
	 */
	public PropertyDescriptor createProperty(int maxValues) {
		return maxValues == 1 ?
			new BooleanProperty("testBoolean", "Test boolean property", false, 1.0f) :
			new BooleanProperty("testBoolean", "Test boolean property", new boolean[] {false}, 1.0f, maxValues);
	}
}
