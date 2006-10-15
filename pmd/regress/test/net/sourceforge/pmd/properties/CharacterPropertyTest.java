package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.CharacterProperty;

/**
 */
public class CharacterPropertyTest extends AbstractPropertyDescriptorTester {

	private static final char delimiter = '|';
	private static final char[] charSet = filter(allChars.toCharArray(), delimiter);
	
	public CharacterPropertyTest() {
		super();
	}

	/**
	 * Method createValue.
	 * @param count int
	 * @return Object
	 */
	protected Object createValue(int count) {
		
		if (count == 1) return new Character(randomChar(charSet));
		
		Character[] values = new Character[count];
		for (int i=0; i<values.length; i++) values[i] = (Character)createValue(1);
		return values;
	}

	/**
	 * Method createProperty.
	 * @param maxCount int
	 * @return PropertyDescriptor
	 */
	protected PropertyDescriptor createProperty(int maxCount) {
		
		return maxCount == 1 ?
			new CharacterProperty("testCharacter", "Test character property", 'a', 1.0f) :
			new CharacterProperty("testCharacter", "Test character property", new char[] {'a', 'b', 'c'}, 1.0f, delimiter);
	}

}
