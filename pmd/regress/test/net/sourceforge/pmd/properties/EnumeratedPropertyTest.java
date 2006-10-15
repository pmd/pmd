package test.net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.EnumeratedProperty;

/**
 */
public class EnumeratedPropertyTest extends AbstractPropertyDescriptorTester {

	private static final Object[][] mixedItems = new Object[][] {
		{"map",			new HashMap()},
		{"emptyArray",	new Object[0]},
		{"list",		new ArrayList()},
		{"string",		"Hello World!"},
		};
	
	public EnumeratedPropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return ((Object[])randomChoice(mixedItems))[1];
		
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
			new EnumeratedProperty("testEnumerations", "Test enumerations with complex types", mixedItems, 1.0f) :
			new EnumeratedProperty("testEnumerations", "Test enumerations with complex types", mixedItems, 1.0f, 3);	
	}

}
