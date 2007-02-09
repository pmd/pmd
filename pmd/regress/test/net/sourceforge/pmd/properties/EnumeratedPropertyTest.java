package test.net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.EnumeratedProperty;

/**
 */
public class EnumeratedPropertyTest extends AbstractPropertyDescriptorTester {

	private static final String[] keys = new String[] {
		"map",
		"emptyArray",
		"list",
		"string",
		};

    private static final Object[] values = new Object[] {
        new HashMap(),
        new Object[0],
        new ArrayList(),
        "Hello World!",
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
		
		if (count == 1) return randomChoice(values);
		
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
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, 1.0f) :
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, 1.0f, 3);	
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EnumeratedPropertyTest.class);
    }
}
