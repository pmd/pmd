/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a property type that supports Boolean values.
 * 
 * @author Brian Remedios
 */
public class BooleanProperty extends AbstractScalarProperty {

	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValue boolean
	 * @param theUIOrder float
	 */
	public BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder) {
		super(theName, theDescription, Boolean.valueOf(defaultValue), theUIOrder);
		
		isMultiValue(false);
	}

	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues boolean[]
	 * @param theUIOrder float
	 */
	public BooleanProperty(String theName, String theDescription, boolean[] defaultValues, float theUIOrder) {
		this(theName, theDescription, asBooleans(defaultValues), theUIOrder);
		
	}
	
	/**
	 * Constructor for BooleanProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Boolean[]
	 * @param theUIOrder float
	 */
	public BooleanProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	/**
	 * Method asBooleans.
	 * @param bools boolean[]
	 * @return Boolean[]
	 */
	private static final Boolean[] asBooleans(boolean[] bools) {
		Boolean[] booleans = new Boolean[bools.length];
		for (int i=0; i<bools.length; i++) {
		    booleans[i] = Boolean.valueOf(bools[i]);
		}
		return booleans;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Boolean> type() {
		return Boolean.class;
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Boolean.valueOf(value);
	}

	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected Object[] arrayFor(int size) {
		return new Boolean[size];
	}
}
