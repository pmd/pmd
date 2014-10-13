package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.DoubleMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;

/**
 * Evaluates the functionality of the DoubleProperty descriptor by testing its ability to catch creation
 * errors (illegal args), flag out-of-range test values, and serialize/deserialize groups of double values
 * onto/from a string buffer.
 * 
 * @author Brian Remedios
 */
public class DoublePropertyTest extends AbstractPropertyDescriptorTester {

	private static final double MIN = -10.0;
	private static final double MAX = 100.0;
	private static final double SHIFT = 5.0;
	
	public DoublePropertyTest() {
		super();
	}

	/**
	 * Creates and returns (count) number of legal Double values
	 * 
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return Double.valueOf(randomDouble(MIN, MAX));
		
		Double[] values = new Double[count];
		for (int i=0; i<values.length; i++) values[i] = (Double)createValue(1);
		return values;
	}

	/**
	 * Creates and returns (count) number of out-of-range values
	 * 
	 * @param count int
	 * @return Object
	 */
	protected Object createBadValue(int count) {
		
		if (count == 1) return Double.valueOf(
				randomBool() ?
						randomDouble(MIN - SHIFT, MIN - 0.01) :
						randomDouble(MAX + 0.01, MAX + SHIFT)
						);
		
		Double[] values = new Double[count];
		for (int i=0; i<values.length; i++) values[i] = (Double)createBadValue(1);
		return values;
	}
	
	/**
	 * Creates and returns a property with a (maxCount) value cardinality.
	 * 
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(boolean multiValue) {
		
		return multiValue ?
			new DoubleMultiProperty("testDouble", "Test double property", MIN, MAX, new Double[] {-1d,0d,1d,2d}, 1.0f) :
			new DoubleProperty("testDouble", "Test double property", MIN, MAX, 9.0, 1.0f);	
	}

	/**
	 * Attempts to create a property with invalid constructor arguments.
	 * 
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new DoubleMultiProperty("testDouble", "Test double property", MIN, MAX, new Double[] {MIN-SHIFT,MIN,MIN+SHIFT,MAX+SHIFT}, 1.0f) :
			new DoubleProperty("testDouble", "Test double property", MAX, MIN, 9.0, 1.0f) ;				
		}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DoublePropertyTest.class);
    }
}
