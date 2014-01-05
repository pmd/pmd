/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.Map;

/**
 * Property value descriptor that defines the use & requirements for setting
 * property values for use within PMD and any associated GUIs. While concrete
 * descriptor instances are static and immutable they provide validation,
 * serialization, and default values for any specific datatypes.
 * 
 * @author Brian Remedios
 * @param <T>
 */
public interface PropertyDescriptor<T extends Object> extends Comparable<PropertyDescriptor<?>> {
    /**
     * The name of the property without spaces as it serves as the key into the
     * property map.
     * 
     * @return String
     */
    String name();

    /**
     * Describes the property and the role it plays within the rule it is
     * specified for. Could be used in a tooltip.
     * 
     * @return String
     */
    String description();

    /**
     * Denotes the value datatype.
     * 
     * @return Class
     */
    Class<T> type();

    /**
     * Returns whether the property is multi-valued, i.e. an array of strings,
     * 
     * As unary property rule properties will return a value of one, you must
     * use the get/setProperty accessors when working with the actual values.
     * When working with multi-value properties then the get/setProperties
     * accessors must be used.
     * 
     * @return boolean
     */
    boolean isMultiValue();

    /**
     * Default value to use when the user hasn't specified one or when they wish
     * to revert to a known-good state.
     * 
     * @return Object
     */
    T defaultValue();

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
     * Denotes the relative order the property field should occupy if we are
     * using an auto-generated UI to display and edit property values. If the
     * value returned has a non-zero fractional part then this is can be used to
     * place adjacent fields on the same row. Example:
     * 
     * name -> 0.0 description 1.0 minValue -> 2.0 maxValue -> 2.1
     * 
     * ..would have their fields placed like:
     * 
     * name: [ ] description: [ ] minimum: [ ] maximum: [ ]
     * 
     * @return float
     */
    float uiOrder();

    /**
     * If the property is multi-valued then return the separate values after
     * parsing the propertyString provided. If it isn't a multi-valued property
     * then the value will be returned within an array of size[1].
     * 
     * @param propertyString String
     * @return Object
     * @throws IllegalArgumentException if the given string cannot be parsed
     */
    T valueFrom(String propertyString) throws IllegalArgumentException;

    /**
     * Formats the object onto a string suitable for storage within the property
     * map.
     * 
     * @param value Object
     * @return String
     */
    String asDelimitedString(T value);

    /**
     * Returns a set of choice tuples if available, returns null if none are
     * defined.
     * 
     * @return Object[][]
     */
    Object[][] choices();

    /**
     * A convenience method that returns an error string if the rule holds onto
     * a property value that has a problem. Returns null otherwise.
     * 
     * @param rule Rule
     * @return String
     */
    String propertyErrorFor(Rule rule);

    /**
     * Return the character being used to delimit multiple property values
     * within a single string. You must ensure that this character does not
     * appear within any rule property values to avoid deserialization errors.
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

    /**
     * Returns a map representing all the property attributes of the receiver in
     * string form.
     * 
     * @return Map<String, String>
     */
    Map<String, String> attributeValuesById();
}
