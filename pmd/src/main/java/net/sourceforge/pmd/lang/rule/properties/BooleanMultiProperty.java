/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a property type that supports multiple Boolean values.
 * 
 * @author Brian Remedios
 */
public class BooleanMultiProperty extends AbstractScalarProperty<Boolean[]> {
	/**
	 * Constructor for BooleanMultiProperty that allows for multiple values.
	 * 
	 * @param theName String
	 * @param theDescription String
	 * @param defaultValues Boolean[]
	 * @param theUIOrder float
	 */
	public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
		super(theName, theDescription, defaultValues, theUIOrder);
	}
	
	/**
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Boolean[]> type() {
		return Boolean[].class;
	}

	/**
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
	 */
	@Override
	public boolean isMultiValue() {
		return true;
	}
	
	/**
	 * Creates and returns a Boolean instance from a raw string
	 * 
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return Boolean.valueOf(value);
	}

	/**
	 * @param size int
	 * @return Object[]
	 */
	protected Boolean[] arrayFor(int size) {
		return new Boolean[size];
	}
	
    /**
     * @return String
     */
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
