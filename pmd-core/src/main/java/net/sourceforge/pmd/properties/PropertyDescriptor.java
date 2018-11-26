/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Property value descriptor that defines the use &amp; requirements for setting property values for use within PMD and
 * any associated GUIs. While concrete descriptor instances are static and immutable they provide validation,
 * serialization, and default values for any specific datatypes.
 *
 * <h1>Upcoming API changes to the properties framework</h1>
 * see <a href="https://github.com/pmd/pmd/issues/1432">pmd/pmd#1432</a>
 *
 * @param <T> type of the property's value. This is a list type for multi-valued properties.
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 */
public interface PropertyDescriptor<T> extends Comparable<PropertyDescriptor<?>> {

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
     *
     * @return A diagnostic message.
     *
     * @deprecated PMD 7.0.0 will change the return type to {@code Optional<String>}
     */
    @Deprecated
    String errorFor(T value); // TODO Java 1.8 make optional


    /**
     * Denotes the value datatype. For multi value properties, this is not the List class but the list's component
     * class.
     *
     * @return Class literal of the value type
     *
     * @deprecated This method is mainly used for documentation, but will not prove general enough
     * to support PMD 7.0.0's improved property types.
     */
    @Deprecated
    Class<?> type();


    /**
     * Returns whether the property is multi-valued, i.e. an array of strings,
     *
     * <p>As unary property rule properties will return a value of one, you must use the get/setProperty accessors when
     * working with the actual values. When working with multi-value properties then the get/setProperties accessors
     * must be used.</p>
     *
     * @return boolean
     *
     * @deprecated The hard divide between multi- and single-value properties will be removed with 7.0.0
     */
    @Deprecated
    boolean isMultiValue();


    /**
     * Denotes the relative order the property field should occupy if we are using an auto-generated UI to display and
     * edit property values. If the value returned has a non-zero fractional part then this is can be used to place
     * adjacent fields on the same row.
     *
     * @return The relative order compared to other properties of the same rule
     *
     * @deprecated This method confuses the presentation layer and the business logic. The order of the
     * property in a UI is irrelevant to the functioning of the property in PMD. With PMD 7.0.0, this
     * method will be removed. UI and documentation tools will decide on their own convention.
     */
    @Deprecated
    float uiOrder();


    /**
     * @deprecated Comparing property descriptors is not useful within PMD
     */
    @Deprecated
    @Override
    int compareTo(PropertyDescriptor<?> o);


    /**
     * Returns the value represented by this string.
     *
     * @param propertyString The string to parse
     *
     * @return The value represented by the string
     *
     * @throws IllegalArgumentException if the given string cannot be parsed
     * @deprecated PMD 7.0.0 will use a more powerful scheme to represent values than
     * simple strings, this method won't be general enough
     */
    @Deprecated
    T valueFrom(String propertyString) throws IllegalArgumentException;


    /**
     * Formats the object onto a string suitable for storage within the property map.
     *
     * @param value Object
     *
     * @return String
     *
     * @deprecated PMD 7.0.0 will use a more powerful scheme to represent values than
     * simple strings, this method won't be general enough
     */
    @Deprecated
    String asDelimitedString(T value);


    /**
     * A convenience method that returns an error string if the rule holds onto a property value that has a problem.
     * Returns null otherwise.
     *
     * @param rule Rule
     *
     * @return String
     *
     * @deprecated Used nowhere, and fails if the rule doesn't define the property descriptor
     * A better solution will be added on property source
     */
    @Deprecated
    String propertyErrorFor(Rule rule);


    /**
     * If the datatype is a String then return the preferred number of rows to allocate in the text widget, returns a
     * value of one for all other types. Useful for multi-line XPATH editors.
     *
     * @deprecated Was never implemented, and is none of the descriptor's concern. Will be removed with 7.0.0
     * @return int
     */
    @Deprecated
    int preferredRowCount();


    /**
     * Returns a map representing all the property attributes of the receiver in string form.
     *
     * @deprecated Will be removed with 7.0.0
     * @return map
     */
    @Deprecated
    Map<PropertyDescriptorField, String> attributeValuesById();


    /**
     * True if this descriptor was defined in the ruleset xml. This precision is necessary for the {@link RuleSetWriter}
     * to write out the property correctly: if it was defined externally, then its definition must be written out,
     * otherwise only its value.
     *
     * @deprecated May be removed with 7.0.0
     * @return True if the descriptor was defined in xml
     */
    @Deprecated
    @InternalApi
    boolean isDefinedExternally();

}
