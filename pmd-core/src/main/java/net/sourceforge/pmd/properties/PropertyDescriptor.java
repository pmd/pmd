/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.PropertyDescriptorField.DEFAULT_VALUE;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.DELIMITER;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.DESCRIPTION;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.NAME;

import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetWriter;


/**
 * Property value descriptor that defines the use &amp; requirements for setting property values for use within PMD and
 * any associated GUIs. While concrete descriptor instances are static and immutable they provide validation,
 * serialization, and default values for any specific datatypes.
 *
 * <p>This interface is primarily specialized according to whether the property is multi-valued or single-valued, see
 * {@link SingleValuePropertyDescriptor} and {@link MultiValuePropertyDescriptor}.
 *
 * <p>Several interfaces further specialize the behaviour of descriptors to accommodate specific types of descriptors,
 * see {@link NumericPropertyDescriptor} and {@link EnumeratedPropertyDescriptor}.
 *
 * @param <T> type of the property's value. This is a list type for multi-valued properties.
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public interface PropertyDescriptor<T> extends Comparable<PropertyDescriptor<?>> {

    /** Default expected fields. Unmodifiable. */
    Map<PropertyDescriptorField, Boolean> CORE_EXPECTED_FIELDS
            = ExpectedFieldsBuilder.instance()
                                   .put(NAME, true)
                                   .put(DESCRIPTION, true)
                                   .put(DEFAULT_VALUE, true)
                                   .put(DELIMITER, false)
                                   .build();


    /**
     * The name of the property without spaces as it serves as the key into the property map.
     *
     * @return String
     */
    String name();


    /**
     * Describes the property and the role it plays within the rule it is specified for. Could be used in a tooltip.
     *
     * @return String
     */
    String description();


    /**
     * Denotes the value datatype. For multi value properties, this is not the List class but the list's component
     * class.
     *
     * @return Class literal of the value type
     */
    Class<?> type();


    /**
     * Returns whether the property is multi-valued, i.e. an array of strings,
     *
     * <p>As unary property rule properties will return a value of one, you must use the get/setProperty accessors when
     * working with the actual values. When working with multi-value properties then the get/setProperties accessors
     * must be used.</p>
     *
     * @return boolean
     */
    boolean isMultiValue();


    /**
     * Default value to use when the user hasn't specified one or when they wish to revert to a known-good state.
     *
     * @return Object
     */
    T defaultValue();


    /**
     * Validation function that returns a diagnostic error message for a sample property value. Returns null if the
     * value is acceptable.
     *
     * @param value The value to check.
     * @return A diagnostic message.
     */
    String errorFor(T value);


    /**
     * Denotes the relative order the property field should occupy if we are using an auto-generated UI to display and
     * edit property values. If the value returned has a non-zero fractional part then this is can be used to place
     * adjacent fields on the same row.
     *
     * <p>Example:<br> name -&gt; 0.0 description 1.0 minValue -&gt; 2.0 maxValue -&gt; 2.1 </p> ..would have their
     * fields placed like:<br>
     *
     * <code>name: [ ] description: [ ] minimum: [ ] maximum: [ ]</code>
     *
     * @return float
     */
    float uiOrder();


    /**
     * Returns the value represented by this string.
     *
     * @param propertyString The string to parse
     * @return The value represented by the string
     * @throws IllegalArgumentException if the given string cannot be parsed
     */
    T valueFrom(String propertyString) throws IllegalArgumentException;


    /**
     * Formats the object onto a string suitable for storage within the property map.
     *
     * @param value Object
     * @return String
     */
    String asDelimitedString(T value);


    /**
     * A convenience method that returns an error string if the rule holds onto a property value that has a problem.
     * Returns null otherwise.
     *
     * @param rule Rule
     * @return String
     */
    String propertyErrorFor(Rule rule);


    /**
     * If the datatype is a String then return the preferred number of rows to allocate in the text widget, returns a
     * value of one for all other types. Useful for multi-line XPATH editors.
     *
     * @return int
     */
    int preferredRowCount();


    /**
     * Returns a map representing all the property attributes of the receiver in string form.
     *
     * @return map
     */
    Map<PropertyDescriptorField, String> attributeValuesById();


    /**
     * True if this descriptor was defined in the ruleset xml. This precision is necessary for the {@link RuleSetWriter}
     * to write out the property correctly: if it was defined externally, then its definition must be written out,
     * otherwise only its value.
     *
     * @return True if the descriptor was defined in xml
     */
    boolean isDefinedExternally();

}
