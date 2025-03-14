/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.properties.internal.PropertyParsingUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Decorates an XmlMapper with some {@link PropertyConstraint}s.
 * Those are checked when the value is parsed. This is used to
 * report errors on the most specific failing element.
 */
class ConstraintDecorator<T> extends PropertySerializer<T> {


    private final PropertySerializer<T> propertySerializer;
    private final List<PropertyConstraint<? super T>> constraints;

    ConstraintDecorator(PropertySerializer<T> mapper, List<PropertyConstraint<? super T>> constraints) {
        this.propertySerializer = mapper;
        this.constraints = constraints;
    }

    @Override
    public List<PropertyConstraint<? super T>> getConstraints() {
        return constraints;
    }

    @Override
    public PropertySerializer<T> withConstraint(PropertyConstraint<? super T> t) {
        return new ConstraintDecorator<>(this.propertySerializer, CollectionUtil.plus(this.constraints, t));
    }

    @Override
    public T fromString(@NonNull String attributeData) {
        T t = propertySerializer.fromString(attributeData);
        // perform constraint validation
        PropertyParsingUtil.checkConstraintsThrow(t, constraints);
        return t;
    }

    @Override
    public @NonNull String toString(T value) {
        return propertySerializer.toString(value);
    }

    @Override
    public String toString() {
        return "ConstraintDecorator{"
            + "propertySerializer=" + propertySerializer
            + ", constraints=" + constraints
            + '}';
    }
}
