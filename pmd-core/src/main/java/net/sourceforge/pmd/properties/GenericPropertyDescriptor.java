/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.properties.internal.XmlSyntax;


/**
 * Bound to be the single implementation for PropertyDescriptor in 7.0.0.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
final class GenericPropertyDescriptor<T> implements PropertyDescriptor<T> {


    private final XmlSyntax<T> parser;
    private final PropertyTypeId typeId;
    private final String name;
    private final String description;
    private final T defaultValue;
    private final Set<PropertyConstraint<? super T>> constraints;


    GenericPropertyDescriptor(String name,
                              String description,
                              T defaultValue,
                              Set<PropertyConstraint<? super T>> constraints,
                              XmlSyntax<T> parser,
                              @Nullable PropertyTypeId typeId) {

        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.constraints = constraints;
        this.parser = parser;
        this.typeId = typeId;

        String dftValueError = errorFor(defaultValue);
        if (dftValueError != null) {
            throw new IllegalArgumentException(dftValueError);
        }
    }


    @Override
    public String errorFor(T value) {
        for (PropertyConstraint<? super T> validator : constraints) {
            String error = validator.validate(value);
            if (error != null) {
                return error;
            }

        }
        return null;
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
    public XmlSyntax<T> xmlStrategy() {
        return parser;
    }

    @Nullable
    @Override
    public PropertyTypeId getTypeId() {
        return typeId;
    }
}
