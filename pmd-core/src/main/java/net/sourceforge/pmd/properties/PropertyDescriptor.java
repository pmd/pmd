/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.properties.xml.XmlMapper;
import net.sourceforge.pmd.properties.xml.XmlSyntaxUtils;


/**
 * Describes a property of a rule or a renderer.
 * <p>Usage of this API is described on {@link PropertyFactory}.
 *
 * <p>A property descriptor provides validation,
 * serialization, and default values for a datatype {@code <T>}.
 * Property descriptors are immutable and can be shared freely.
 *
 * <h1>Upcoming API changes to the properties framework</h1>
 * see <a href="https://github.com/pmd/pmd/issues/1432">pmd/pmd#1432</a>
 *
 * @param <T> Type of the property's value
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version 7.0.0
 * @see PropertyFactory
 * @see PropertyBuilder
 */
public final class PropertyDescriptor<T> {


    private final XmlMapper<T> parser;
    private final PropertyTypeId typeId;
    private final String name;
    private final String description;
    private final T defaultValue;
    private final boolean isXPathAvailable;

    PropertyDescriptor(String name,
                       String description,
                       T defaultValue,
                       XmlMapper<T> parser,
                       @Nullable PropertyTypeId typeId,
                       boolean isXPathAvailable) {

        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.parser = parser;
        this.typeId = typeId;
        this.isXPathAvailable = isXPathAvailable;

        XmlSyntaxUtils.checkConstraintsThrow(
            defaultValue,
            parser.getConstraints(),
            s -> new IllegalArgumentException("Constraint violated " + s)
        );
    }


    /**
     * The name of the property without spaces as it serves as the key
     * into the property map.
     *
     * @return String
     */
    public String name() {
        return name;
    }


    /**
     * Describes the property and the role it plays within the rule it
     * is specified for. Could be used in a tooltip.
     *
     * @return String
     */
    public String description() {
        return description;
    }


    /**
     * Default value to use when the user hasn't specified one or when
     * they wish to revert to a known-good state.
     *
     * @return Object
     */
    public T defaultValue() {
        return defaultValue;
    }


    /**
     * Returns the strategy used to read and write this property to XML.
     * May support strings too.
     */
    public XmlMapper<T> xmlMapper() {
        return parser;
    }


    /**
     * TODO this needs to go away. Property constraints are now checked at
     * the time the ruleset is parsed, to report errors on the specific
     * XML nodes. Other than that, constraints should be checked when
     * calling {@link PropertySource#setProperty(PropertyDescriptor, Object)}
     * for fail-fast behaviour.
     *
     * @deprecated PMD 7.0.0 will change the return type to {@code Optional<String>}
     */
    @Deprecated
    public String errorFor(T value) {
        return XmlSyntaxUtils.checkConstraintsJoin(value, parser.getConstraints());
    }


    /**
     * Returns the type ID which was used to define this property. Returns
     * null if this property was defined in Java code and not in XML. This
     * is used to write the property back to XML, when using a {@link RuleSetWriter}.
     */
    @InternalApi
    public @Nullable PropertyTypeId getTypeId() {
        return typeId;
    }

    /**
     * Returns whether the property is available to XPath queries.
     */
    public boolean isXPathAvailable() {
        return isXPathAvailable;
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
    public T valueFrom(String propertyString) throws IllegalArgumentException {
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
    public String asDelimitedString(T value) {
        return xmlMapper().toString(value);
    }

    @Override
    public String toString() {
        return "PropertyDescriptor{ "
            + "name='" + name + '\''
            + ", parser=" + parser
            + ", typeId=" + typeId
            + ", description='" + description + '\''
            + ", defaultValue=" + defaultValue + '}';
    }

    // TODO these equality routines needs to go away, should be implemented in Rule::equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyDescriptor<?> that = (PropertyDescriptor<?>) o;
        return name.equals(that.name)
            && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
