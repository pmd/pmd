/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;


/**
 * Defines a property type that support single double-type property values within an upper and lower boundary.
 * 
 * @author Brian Remedios
 */
public class DoubleProperty extends AbstractNumericProperty<Double> {

	/**
	 * Constructor for DoubleProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param min double
	 * @param max double
	 * @param theDefault double
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	/**
     * Constructor for DoubleProperty.
     * @param theName String
     * @param theDescription String
     * @param minStr String
     * @param maxStr String
     * @param defaultStr String
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public DoubleProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, doubleFrom(minStr), doubleFrom(maxStr), doubleFrom(defaultStr), theUIOrder);       
    }
	
    
    public static Double doubleFrom(String numberString) {
        return Double.valueOf(numberString);
    }
    
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Double> type() {
		return Double.class;
	}

	/**
	 * Deserializes a string into its Double form.
	 * 
	 * @param value String
	 * @return Object
	 */
	protected Object createFrom(String value) {
		return doubleFrom(value);
	}
}
