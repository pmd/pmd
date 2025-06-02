/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Strategy to serialize a value to and from strings.
 */
public abstract class PropertySerializer<T> {

    PropertySerializer() {
        // package private, we want to control available mappers to
        // put them into the ruleset schema
    }


    /**
     * Returns the constraints that this mapper applies to values
     * after parsing them. This may be used for documentation, or
     * to check a constraint on a value that was not parsed from
     * XML.
     *
     * @implNote See {@link ConstraintDecorator}
     */
    public abstract List<PropertyConstraint<? super T>> getConstraints();

    /**
     * Returns a new XML mapper that will check parsed values with
     * the given constraint.
     */
    public PropertySerializer<T> withConstraint(PropertyConstraint<? super T> t) {
        return new ConstraintDecorator<>(this, Collections.singletonList(t));
    }

    /**
     * Read the value from a string, if it is supported.
     *
     * @throws IllegalArgumentException      if something goes wrong (but should be reported on the error reporter)
     */
    public abstract T fromString(@NonNull String attributeData);

    /**
     * Format the value to a string.
     *
     * @throws IllegalArgumentException      if something goes wrong (but should be reported on the error reporter)
     */
    public abstract @NonNull String toString(T value);


}
