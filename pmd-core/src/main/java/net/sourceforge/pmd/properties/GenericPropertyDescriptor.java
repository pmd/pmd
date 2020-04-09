/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.properties.xml.XmlMapper;
import net.sourceforge.pmd.properties.xml.XmlSyntaxUtils;


/**
 * Bound to be the single implementation for PropertyDescriptor in 7.0.0.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
final class GenericPropertyDescriptor<T> implements PropertyDescriptor<T> {


    private final XmlMapper<T> parser;
    private final PropertyTypeId typeId;
    private final String name;
    private final String description;
    private final T defaultValue;

    GenericPropertyDescriptor(String name,
                              String description,
                              T defaultValue,
                              XmlMapper<T> parser,
                              @Nullable PropertyTypeId typeId) {

        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.parser = parser;
        this.typeId = typeId;

        XmlSyntaxUtils.checkConstraintsThrow(
            defaultValue,
            parser.getConstraints(),
            s -> new IllegalArgumentException("Constraint violated " + s)
        );
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public XmlMapper<T> xmlMapper() {
        return parser;
    }

    @Nullable
    @Override
    public PropertyTypeId getTypeId() {
        return typeId;
    }
}
