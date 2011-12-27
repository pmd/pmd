package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.util.CollectionUtil;

import org.junit.Test;

/**
 * Base functionality for all concrete subclasses that evaluate type-specific property descriptors.
 * Checks for error conditions during construction, error value detection, serialization, etc. 
 *  
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorTester {

	private static final int multiValueCount = 10;
	
	public static final String punctuationChars  = "!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?`~";
	public static final String whitespaceChars   = " \t\n";
	public static final String digitChars 		 = "0123456789";
	public static final String alphaChars 		 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";
	public static final String alphaNumericChars = digitChars + alphaChars;
	public static final String allChars			 = punctuationChars + whitespaceChars + alphaNumericChars;

	
	/**
	 * Return a legal value(s) per the general scope of the descriptor.
	 * 
	 * @param count int
	 * @return Object
	 */
	protected abstract Object createValue(int count);
	
	/**
	 * Return a value(s) that is known to be faulty per the general scope of the descriptor.
	 * 
	 * @param count int
	 * @return Object
	 */
	protected abstract Object createBadValue(int count);
	
	/**
	 * Creates and returns a properly configured property descriptor.
	 * 
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected abstract PropertyDescriptor createProperty(boolean multiValue);
	
	/**
	 * Attempt to create a property with faulty configuration values. This method
	 * should throw an IllegalArgumentException if done correctly.
	 * 
	 * @param multiValue boolean
	 * @return PropertyDescriptor
	 */
	protected abstract PropertyDescriptor createBadProperty(boolean multiValue);
	
	@Test
	public void testConstructors() {
		
		PropertyDescriptor<?> desc = createProperty(false);
		assertNotNull(desc);

		try {
			createBadProperty(false);

		} catch (Exception ex) {
			return;	// caught ok
		}

		Assert.fail("uncaught constructor exception");
	}

    @Test
    public void testAsDelimitedString() {

		Object testValue = createValue(multiValueCount);
		PropertyDescriptor pmdProp = createProperty(true);

		String storeValue = pmdProp.asDelimitedString(testValue);

		Object returnedValue = pmdProp.valueFrom(storeValue);

		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}

    @Test
    public void testValueFrom() {

		Object testValue = createValue(1);
		PropertyDescriptor pmdProp = createProperty(false);

		String storeValue = pmdProp.asDelimitedString(testValue);

		Object returnedValue = pmdProp.valueFrom(storeValue);

		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}
	
	
    @Test
    public void testErrorFor() {

		Object testValue = createValue(1);
		PropertyDescriptor<?> pmdProp = createProperty(false);		// plain vanilla property & valid test value
		String errorMsg = pmdProp.errorFor(testValue);
		assertNull(errorMsg, errorMsg);			

		testValue = createValue(multiValueCount);				// multi-value property, all valid test values
		pmdProp = createProperty(true);
		errorMsg = pmdProp.errorFor(testValue);
		assertNull(errorMsg, errorMsg);

    }
    
    @Test
    public void testErrorForBad() {

    	PropertyDescriptor<?> pmdProp = createProperty(false);    	
		Object testValue = createBadValue(1);
		String errorMsg = pmdProp.errorFor(testValue);			// bad value should result in an error
		if (errorMsg == null) {
			Assert.fail("uncaught bad value: " + testValue);
		}

		testValue = createBadValue(multiValueCount);			// multi-value prop, several bad values
		pmdProp = createProperty(true);
		errorMsg = pmdProp.errorFor(testValue);
		if (errorMsg == null) {
			Assert.fail("uncaught bad value in: " + testValue);
		}
	}

    @Test
    public void testType() {

		PropertyDescriptor<?> pmdProp = createProperty(false);

		assertNotNull(pmdProp.type());
	}

    public static boolean randomBool() {
    	return ((Math.random() * 100) % 2) == 0;
    }

	/**
	 * Method randomInt.
	 * @return int
	 */
	public static int randomInt() {

		int randomVal = (int) (Math.random() * 100 + 1D);
		return randomVal + (int) (Math.random() * 100000D);
	}

	/**
	 * Method randomInt.
	 * @param min int
	 * @param max int
	 * @return int
	 */
	public static int randomInt(int min, int max) {
		if (max < min) max = min;
		int range = Math.abs(max - min);
		int x = (int) (range * Math.random());
		return x + min;
	}
	
	public static String randomString(int length) {
		
		final char[] chars = alphaChars.toCharArray();
		
		StringBuilder sb = new StringBuilder(length);
		for (int i=0; i<length; i++) sb.append(randomChar(chars));
		return sb.toString();
	}
	
	/**
	 * Method randomFloat.
	 * @param min float
	 * @param max float
	 * @return float
	 */
	public static float randomFloat(float min, float max) {
		
		return (float)randomDouble(min, max);
	}
	
	/**
	 * Method randomDouble.
	 * @param min double
	 * @param max double
	 * @return double
	 */
	public static double randomDouble(double min, double max) {
		if (max < min) max = min;
		double range = Math.abs(max - min);
		double x = range * Math.random();
		return x + min;
	}
	
	/**
	 * Method randomChar.
	 * @param characters char[]
	 * @return char
	 */
	public static char randomChar(char[] characters) {
		return characters[randomInt(0, characters.length-1)];
	}
	
	/**
	 * Method randomChoice.
	 * @param items Object[]
	 * @return Object
	 */
	public static Object randomChoice(Object[] items) {
		return items[randomInt(0, items.length-1)];
	}
	
	/**
	 * Method filter.
	 * @param chars char[]
	 * @param removeChar char
	 * @return char[]
	 */
	protected static final char[] filter(char[] chars, char removeChar) {
		int count = 0;
		for (int i=0; i<chars.length; i++) if (chars[i] == removeChar) count++;
		char[] results = new char[chars.length - count];
		
		int index = 0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i] != removeChar) results[index++] = chars[i];		
		}
		return results;
	}
}
