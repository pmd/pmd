/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.properties.PropertyBuilder;


/**
 * Validates the value of a property.
 *
 * @param <T> Type of value to handle
 *
 * @see PropertyBuilder#require(PropertyConstraint)
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public interface PropertyConstraint<T> {

    /**
     * Returns a diagnostic message if the value
     * has a problem. Otherwise returns null.
     *
     * @param value The value to validate
     *
     * @return A diagnostic message
     */
    @Nullable
    String validate(T value);


    /**
     * Returns a description of the constraint
     * imposed by this validator on the values.
     * E.g. "Should be positive", or "Should be one of A | B | C."
     *
     * <p>This is used to generate documentation.
     *
     * @return A description of the constraint
     */
    String getConstraintDescription();

    /**
     * Returns a constraint that validates an {@code Optional<T>}
     * by checking that the value conforms to this constraint if
     * it is non-empty.
     */
    @Experimental
    default PropertyConstraint<Optional<? extends T>> toOptionalConstraint() {
        return new PropertyConstraint<Optional<? extends T>>() {
            @Override
            public @Nullable String validate(Optional<? extends T> value) {
                return value.map(PropertyConstraint.this::validate).orElse(null);
            }

            @Override
            public String getConstraintDescription() {
                return PropertyConstraint.this.getConstraintDescription();
            }
        };
    }


    /**
     * Returns a constraint that validates a collection of Ts
     * by checking each component conforms to this validator.
     *
     * @return A collection validator
     */
    default PropertyConstraint<Iterable<? extends T>> toCollectionConstraint() {
        return new PropertyConstraint<Iterable<? extends T>>() {
            @Override
            public @Nullable String validate(Iterable<? extends T> value) {
                List<String> errors = new ArrayList<>();
                for (T t : value) {
                    String compValidation = PropertyConstraint.this.validate(t);
                    if (compValidation != null) {
                        errors.add(compValidation);
                    }
                }
                return errors.isEmpty() ? null
                                        : String.join(", ", errors);
            }

            @Override
            public String getConstraintDescription() {
                return "Components " + StringUtils.uncapitalize(PropertyConstraint.this.getConstraintDescription());
            }
        };
    }


    /**
     * Builds a new validator from a predicate, and description.
     *
     * @param pred                  The predicate. If it returns
     *                              false on a value, then the
     *                              value is deemed to have a
     *                              problem
     * @param constraintDescription Description of the constraint,
     *                              see {@link PropertyConstraint#getConstraintDescription()}.
     * @param <U>                   Type of value to validate
     *
     * @return A new validator
     */
    @Experimental
    static <U> PropertyConstraint<U> fromPredicate(final Predicate<? super U> pred, final String constraintDescription) {
        return new PropertyConstraint<U>() {

            @Override
            public String validate(U value) {
                return pred.test(value) ? null : "'" + value + "' " + StringUtils.uncapitalize(constraintDescription);
            }

            @Override
            public String getConstraintDescription() {
                return StringUtils.capitalize(constraintDescription);
            }
        };
    }

}
