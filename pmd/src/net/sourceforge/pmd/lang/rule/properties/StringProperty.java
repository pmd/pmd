/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype that supports single String values.
 * 
 * @author Brian Remedios
 */
public class StringProperty extends AbstractProperty<String> {
	
	/**
	 * Constructor for StringProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefaultValue String
	 * @param theUIOrder float
	 */
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
	}
	
    /**
     * @return String
     */
    protected String defaultAsString() {
        return defaultValue();
    }
	
	/**
	 *
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<String> type() {
		return String.class;
	}
	
	/**
	 *
	 * @param valueString String
	 * @return Object
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public String valueFrom(String valueString) {
		return valueString;
	}
}
