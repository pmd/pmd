package test.net.sourceforge.pmd.properties;

import junit.framework.TestCase;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorTester extends TestCase {

	private static final int maxCardinality = 10;
	
	public static final String punctuationChars  = "!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?`~";
	public static final String whitespaceChars   = " \t\n";
	public static final String digitChars 		 = "0123456789";
	public static final String alphaChars 		 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";
	public static final String alphaNumericChars = digitChars + alphaChars;
	public static final String allChars			 = punctuationChars + whitespaceChars + alphaNumericChars;

	
	protected AbstractPropertyDescriptorTester() { }
	
	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected abstract Object createValue(int count);
	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected abstract PropertyDescriptor createProperty(int maxCount);
	
	public void testAsDelimitedString() {
		
		Object testValue = createValue(maxCardinality);
		PropertyDescriptor pmdProp = createProperty(maxCardinality);
		
		String storeValue = pmdProp.asDelimitedString(testValue);
		
		Object returnedValue = pmdProp.valueFrom(storeValue);
		
		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}
	
	public void testValueFrom() {
		
		Object testValue = createValue(1);
		PropertyDescriptor pmdProp = createProperty(1);
		
		String storeValue = pmdProp.asDelimitedString(testValue);
		
		Object returnedValue = pmdProp.valueFrom(storeValue);
		
		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}
	
	
	public void testErrorFor() {
		
		Object testValue = createValue(1);
		PropertyDescriptor pmdProp = createProperty(1);
		String errorMsg = pmdProp.errorFor(testValue);
		assertTrue(errorMsg == null);
		
		testValue = createValue(maxCardinality);
		pmdProp = createProperty(maxCardinality);
		errorMsg = pmdProp.errorFor(testValue);
		assertTrue(errorMsg == null);
	}
	
	public void testType() {
		
		PropertyDescriptor pmdProp = createProperty(1);

		assertTrue(pmdProp.type() != null);
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
		int x = (int) ((range * Math.random()) + .5);
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
