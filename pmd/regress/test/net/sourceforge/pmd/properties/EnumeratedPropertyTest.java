package test.net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * 
 * @author Brian Remedios
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
	 * Returns a (count) number of values that are not in the set of legal values.
	 * 
	 * @param count int
	 * @return Object
	 */
	protected Object createBadValue(int count) {
		
		if (count == 1) return Integer.toString(randomInt());		// not in the set of values
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createBadValue(1);
		return values;
	}
	
	/**
	 * Method createProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(boolean multiValue) {
		
		return multiValue ?
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, new int[] {0,1}, 1.0f) :
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, 0, 1.0f);			
	}

	/**
	 * Method createBadProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, new Object[0], new int[] {99}, 1.0f) :
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", new String[0], values, -1, 1.0f);
	}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EnumeratedPropertyTest.class);
    }
}
