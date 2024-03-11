/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.rule.RuleSetWriter;
import net.sourceforge.pmd.properties.internal.PropertyParsingUtil;
import net.sourceforge.pmd.properties.internal.PropertyTypeId;


/**
 * Describes a property of a rule or a renderer.
 * <p>Usage of this API is described on {@link PropertyFactory}.
 *
 * <p>A property descriptor provides validation,
 * serialization, and default values for a datatype {@code <T>}.
 * Property descriptors are immutable and can be shared freely.
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


    private final PropertySerializer<T> parser;
    private final PropertyTypeId typeId;
    private final String name;
    private final String description;
    private final T defaultValue;
    private final boolean isXPathAvailable;

    PropertyDescriptor(String name,
                       String description,
                       T defaultValue,
                       PropertySerializer<T> parser,
                       @Nullable PropertyTypeId typeId,
                       boolean isXPathAvailable) {

        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.parser = parser;
        this.typeId = typeId;
        this.isXPathAvailable = isXPathAvailable;

        PropertyParsingUtil.checkConstraintsThrow(
            defaultValue,
            parser.getConstraints()
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
    public PropertySerializer<T> serializer() {
        return parser;
    }

    /**
     * Returns the type ID which was used to define this property. Returns
     * null if this property was defined in Java code and not in XML. This
     * is used to write the property back to XML, when using a {@link RuleSetWriter}.
     *
     * @apiNote Internal API
     */
    @Nullable PropertyTypeId getTypeId() {
        return typeId;
    }

    /**
     * Returns whether the property is available to XPath queries.
     */
    public boolean isXPathAvailable() {
        return isXPathAvailable;
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
