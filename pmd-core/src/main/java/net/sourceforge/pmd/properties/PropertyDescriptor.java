/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.internal.ValueSyntax;
import net.sourceforge.pmd.properties.internal.XmlSyntax;


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
public interface PropertyDescriptor<T> {

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
     * Returns the strategy used to read and write this property to XML.
     * May support strings too.
     */
    default XmlSyntax<T> xmlStrategy() {
        return new ValueSyntax<>(this::asDelimitedString, this::valueFrom);
    }


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
    default String errorFor(T value) {
        return null;
    }


    /**
     * Returns the type ID which was used to define this property. Returns
     * null if this property was defined in Java code and not in XML.
     */
    default @Nullable PropertyTypeId getTypeId() {
        return null;
    }


    /**
     * Returns the value represented by this string.
     *
     * @param propertyString The string to parse
     *
     * @return The value represented by the string
     *
     * @throws IllegalArgumentException if the given string cannot be parsed
     * @throws UnsupportedOperationException If operation is not supported
     * @deprecated PMD 7.0.0 will use a more powerful scheme to represent values than
     * simple strings, this method won't be general enough
     */
    @Deprecated
    default T valueFrom(String propertyString) throws IllegalArgumentException {
        return xmlStrategy().fromString(propertyString);
    }


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
    default String asDelimitedString(T value) {
        return xmlStrategy().toString(value);
    }


}
