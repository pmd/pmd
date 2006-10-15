package net.sourceforge.pmd;


/**
 * Property value descriptor that defines the use & requirements for setting
 * property values for use within PMD and any associated GUIs.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public interface PropertyDescriptor extends Comparable {

	PropertyDescriptor[] emptyPropertySet = new PropertyDescriptor[0];
	
	/**
	 * The name of the property without spaces as it serves 
	 * as the key into the property map.
	 * 
	 * @return String
	 */
	String name();
	/**
	 * Describes the property and the role it plays within the
	 * rule it is specified for. Could be used in a tooltip.
	 * 
	 * @return String
	 */
	String description();
	/**
	 * Denotes the value datatype.
	 * @return Class
	 */
	Class type();
	/**
	 * If the property is multi-valued, i.e. an array of strings, then this
	 * returns the maximum number permitted. Unary property rule properties
	 * normally return a value of one.
	 * 
	 * @return int
	 */
	int maxValueCount();
	/**
	 * Default value to use when the user hasn't specified one or when they wish
	 * to revert to a known-good state.
	 * 
	 * @return Object
	 */
	Object defaultValue();
	/**
	 * Denotes whether the value is required before the rule can be executed.
	 * Has no meaning for primitive types such as booleans, ints, etc.
	 * 
	 * @return boolean
	 */
	boolean isRequired();
	/**
	 * Validation function that returns a diagnostic error message for a sample
	 * property value. Returns null if the value is acceptable.
	 * 
	 * @param value Object
	 * @return String
	 */
	String errorFor(Object value);	
	/**
	 * Denotes the relative order the property field should occupy if we are using 
	 * an auto-generated UI to display and edit values. If the value returned has
	 * a non-zero fractional part then this is can be used to place adjacent fields 
	 * on the same row. Example:
	 * 
	 * name -> 0.0
	 * description 1.0
	 * minValue -> 2.0
	 * maxValue -> 2.1
	 * 
	 * ..would have their fields placed like:
	 * 
	 *  name: [    ]
	 *  description: [    ]
	 *  minimum: [    ]   maximum:   [    ]
	 *  
	 * @return float
	 */
	float uiOrder();
	/**
	 * If the property is multi-valued then return the separate values after
	 * parsing the propertyString provided. If it isn't a multi-valued
	 * property then the value will be returned within an array of size[1].
	 * 
	 * @param propertyString String
	 * @return Object
	 * @throws IllegalArgumentException
	 */
	Object valueFrom(String propertyString) throws IllegalArgumentException;
	/**
	 * Formats the object onto a string suitable for storage within the property map.
	 * @param value Object
	 * @return String
	 */
	String asDelimitedString(Object value);
	
	/**
	 * Returns a set of choice tuples of available, returns null if none present.
	 * @return Object[]
	 */
	Object[][] choices();
	
	/**
	 * A convenience method that returns an error string if the rule holds onto a
	 * property value that has a problem. Returns null otherwise.
	 * 
	 * @param rule Rule
	 * @return String
	 */
	String propertyErrorFor(Rule rule);
	
	/**
	 * Return the character being used to delimit multiple property values within
	 * a single string. You must ensure that this character does not appear within
	 * any rule property values to avoid deserialization errors.
	 * 
	 * @return char
	 */
	char multiValueDelimiter();
	
	/**
	 * If the datatype is a String then return the preferred number of rows to
	 * allocate in the text widget, returns a value of one for all other types.
	 * Useful for multi-line XPATH editors.
	 * 
	 * @return int
	 */
	int preferredRowCount();
}
