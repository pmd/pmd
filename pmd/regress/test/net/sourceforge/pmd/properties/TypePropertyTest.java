package test.net.sourceforge.pmd.properties;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.TypeProperty;

/**
 */
public class TypePropertyTest extends AbstractPropertyDescriptorTester {

	public static final Class[] classes = new Class[] { String.class, Integer.class, int.class, HashMap.class, Map.class };
	
	public TypePropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {

		if (count == 1) return randomChoice(classes);
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		
		return maxCount == 1 ?
			new TypeProperty("testType", "Test type property", Byte.class, 1.0f) :
			new TypeProperty("testType", "Test type property", classes, 1.0f);
			}

}
