package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

import org.junit.Test;

/**
 * Evaluates the functionality of the IntegerProperty descriptor by testing its ability to catch creation
 * errors (illegal args), flag out-of-range test values, and serialize/deserialize groups of integers
 * onto/from a string buffer.
 * 
 * @author Brian Remedios
 */
public class IntegerPropertyTest extends AbstractPropertyDescriptorTester {

	private static final int MIN = 1;
	private static final int MAX = 12;
	private static final int SHIFT = 3;

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {

		if (count == 1) return Integer.valueOf(randomInt(MIN, MAX));

		Integer[] values = new Integer[count];
		for (int i=0; i<values.length; i++) values[i] = (Integer)createValue(1);
		return values;
	}

	/**
	 * Creates and returns (count) number of out-of-range Integer values
	 * 
	 * @param count int
	 * @return Object
	 */
	protected Object createBadValue(int count) {

		if (count == 1) return Integer.valueOf(
			randomBool() ?
					randomInt(MIN - SHIFT, MIN) :
					randomInt(MAX, MAX + SHIFT)
					);

		Integer[] values = new Integer[count];
		for (int i=0; i<values.length; i++) values[i] = (Integer)createBadValue(1);
		return values;
	}

	 @Test
	public void testErrorForBad() { }	// not until int properties get ranges

	/**
	 * Method createProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(boolean multiValue) {

		return multiValue ?
			new IntegerMultiProperty("testInteger", "Test integer property", MIN, MAX, new Integer[] {MIN, MIN+1, MAX-1, MAX}, 1.0f) :
			new IntegerProperty("testInteger", "Test integer property", MIN, MAX, MAX-1, 1.0f);
		}

	/**
	 * Method createBadProperty.
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {

		return multiValue ?
			new IntegerMultiProperty("testInteger", "", MIN, MAX, new Integer[] {MIN-1, MAX}, 1.0f) :
			new IntegerProperty("", "Test integer property", MIN, MAX, MAX+1, 1.0f); 
		}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(IntegerPropertyTest.class);
    }
}
