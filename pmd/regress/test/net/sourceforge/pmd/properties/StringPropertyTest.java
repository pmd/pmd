package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.StringProperty;

/**
 */
public class StringPropertyTest extends AbstractPropertyDescriptorTester {

	private static final int maxStringLength = 52;
	private static final char delimiter = '|';
	private static final char[] charSet = filter(allChars.toCharArray(), delimiter);
	
	public StringPropertyTest() {
		super();
	}
	
	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {

		if (count == 1) return newString();
		
		String[] values = new String[count];
		for (int i=0; i<count; i++) values[i] = (String)createValue(1);
		return values;
	}

	/**
	 * Method newString.
	 * @return String
	 */
	private String newString() {
		
		int strLength = randomInt(0, maxStringLength);
		
		char[] chars = new char[strLength];
		for (int i=0; i<chars.length; i++) chars[i] = randomCharIn(charSet);
		return new String(chars);
	}
	
	/**
	 * Method randomCharIn.
	 * @param chars char[]
	 * @return char
	 */
	private char randomCharIn(char[] chars) {
		return randomChar(chars);
	}
	
	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		return maxCount == 1 ?
			new StringProperty("testString", "Test string property", "brian", 1.0f) :
			new StringProperty("testString", "Test string property", new String[] {"hello", "world"}, 1.0f, delimiter);
		}

}
