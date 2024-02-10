/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.properties.InternalApiBridge;
import net.sourceforge.pmd.properties.PropertyConstraint;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <value>1</value>
 *  <value>someString</value>
 *  <value>1,2,3</value>
 * }</pre>
 *
 * <pre>This class is special because it enables compatibility with the
 * pre 7.0.0 XML syntax.
 */
class ValueSyntax<T> extends InternalApiBridge.InternalPropertySerializer<T> {

    private final Function<? super T, @NonNull String> toString;
    private final Function<@NonNull String, ? extends T> fromString;

    // these are not applied, just used to document the possible values
    private final List<PropertyConstraint<? super T>> docConstraints;

    ValueSyntax(Function<? super T, String> toString,
                Function<@NonNull String, ? extends T> fromString,
                List<PropertyConstraint<? super T>> docConstraints) {
        this.toString = toString;
        this.fromString = fromString;
        this.docConstraints = docConstraints;
    }

    @Override
    public List<PropertyConstraint<? super T>> getConstraints() {
        return docConstraints;
    }

    @Override
    public T fromString(@NonNull String attributeData) {
        return fromString.apply(attributeData);
    }

    @Override
    public @NonNull String toString(T data) {
        return toString.apply(data);
    }

    /**
     * Creates a value syntax that cannot parse just any string, but
     * which only applies the fromString parser if a precondition holds.
     * The precondition is represented by a constraint on strings, and
     * is documented as a constraint on the returned XML mapper.
     */
    static <T> ValueSyntax<T> partialFunction(Function<? super T, @NonNull String> toString,
                                              Function<@NonNull String, ? extends T> fromString,
                                              PropertyConstraint<? super @NonNull String> checker) {
        PropertyConstraint<T> docConstraint = PropertyConstraint.fromPredicate(
            PredicateUtil.always(),
            checker.getConstraintDescription()
        );

        return new ValueSyntax<>(
            toString,
            s -> {
                checker.validate(s);
                return fromString.apply(s);
            },
            listOf(docConstraint)
        );
    }

    static <T> ValueSyntax<T> withDefaultToString(Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(Objects::toString, fromString, Collections.emptyList());
    }

    static <T> ValueSyntax<T> create(Function<? super T, String> toString,
                                     Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(toString, fromString, Collections.emptyList());
    }
}
