package test.net.sourceforge.pmd.properties;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;

/**
 * 
 * @author Brian Remedios
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
	 * Method createBadValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createBadValue(int count) {

		if (count == 1) return null;
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createBadValue(1);
		return values;
	}
	
	 @Test
	public void testErrorForBad() { }	// not until type properties get illegal packages
	
	
	/**
	 * Method createProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(boolean multiValue) {
		
		return multiValue ?
			new TypeProperty("testType", "Test type property", classes, 1.0f) :
			new TypeProperty("testType", "Test type property", Byte.class, 1.0f);
			}

	/**
	 * Method createProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new TypeProperty("testType", "Test type property", new Class[0], 1.0f) :
			new TypeProperty("testType", "", Byte.class, 1.0f);
			}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TypePropertyTest.class);
    }
}
