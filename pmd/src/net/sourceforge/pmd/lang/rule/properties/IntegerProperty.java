/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype that supports the Integer property values.
 * 
 * @author Brian Remedios
 */
public class IntegerProperty extends AbstractScalarProperty {

	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault int
	 * @param theUIOrder float
	 */
	public IntegerProperty(String theName, String theDescription, int theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
		
		isMultiValue(false);
	}

	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults int[]
	 * @param theUIOrder float
	 */
	public IntegerProperty(String theName, String theDescription, int[] theDefaults, float theUIOrder) {
		this(theName, theDescription, asIntegers(theDefaults), theUIOrder);
	}
	
	/**
	 * Constructor for IntegerProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaults Integer[]
	 * @param theUIOrder float
	 */
	public IntegerProperty(String theName, String theDescription, Integer[] theDefaults, float theUIOrder) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		isMultiValue(true);
	}
	
	/**
	 * Method asIntegers.
	 * @param ints int[]
	 * @return Integer[]
	 */
	private static final Integer[] asIntegers(int[] ints) {
		Integer[] integers = new Integer[ints.length];
		for (int i=0; i<ints.length; i++) {
		    integers[i] = Integer.valueOf(ints[i]);
		}
		return integers;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Integer> type() {
		return Integer.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Integer.valueOf(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Integer[size];
	}
}
