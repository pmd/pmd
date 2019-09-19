/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.properties.xml.XmlMapper;


/**
 * Describes a property of a rule or a renderer.
 * <p>Usage of this API is described on {@link PropertyFactory}.
 *
 * <p>A property descriptor provides validation,
 * serialization, and default values for a datatype {@code <T>}.
 * Property descriptors are immutable and can be shared freely.
 * Property descriptors do not override {@link Object#equals(Object)}
 * or {@link Object#hashCode()}. Pre 6.0.0 two descriptors were equal
 * if they had the same name.
 *
 * <h1>Upcoming API changes to the properties framework</h1>
 * see <a href="https://github.com/pmd/pmd/issues/1432">pmd/pmd#1432</a>
 *
 * @param <T> Type of the property's value.
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version 7.0.0
 * @see PropertyFactory
 * @see PropertyBuilder
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
    XmlMapper<T> xmlMapper();


    /**
     * TODO
     *  this needs to go away. Property constraints should be checked
     *  at the time the ruleset is parsed, to report error messages
     *  targeted on each node. They could simply decorate the XmlMapper.
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
     *
     * TODO this replaces isDefinedExternally for the RulesetWriter.
     * I still don't like it.
     */
    @InternalApi
    default @Nullable PropertyTypeId getTypeId() {
        return null;
    }


    /**
     * TODO port tests to use the mapper directly.
     *
     * @throws IllegalArgumentException      if the given string cannot be parsed
     * @throws UnsupportedOperationException If operation is not supported
     * @deprecated PMD 7.0.0 will use a more powerful scheme to represent values than
     *     simple strings, this method won't be general enough
     */
    @Deprecated
    default T valueFrom(String propertyString) throws IllegalArgumentException {
        return xmlMapper().fromString(propertyString);
    }


    /**
     * TODO port tests to use the mapper directly.
     *
     * @param value Object
     *
     * @return String
     *
     * @throws UnsupportedOperationException If operation is not supported
     * @deprecated PMD 7.0.0 will use a more powerful scheme to represent values than
     *     simple strings, this method won't be general enough
     */
    @Deprecated
    default String asDelimitedString(T value) {
        return xmlMapper().toString(value);
    }


}
