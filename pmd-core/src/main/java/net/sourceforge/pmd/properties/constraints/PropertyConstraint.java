/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;


/**
 * Validates the value of a property.
 *
 * <p>This interface will change a lot with PMD 7.0.0,
 * because of the switch to Java 8. Please use
 * only the ready-made validators in {@link NumericConstraints}
 * for now.
 *
 * @param <T> Type of value to handle
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
@Experimental
public interface PropertyConstraint<T> {

    /**
     * Returns a diagnostic message if the value
     * has a problem. Otherwise returns an empty
     * optional.
     *
     * @param value The value to validate
     *
     * @return An optional diagnostic message
     */
    @Nullable
    String validate(T value); // Future make default


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

            // TODO message could be better, eg include name of the property
            @Override
            public String validate(U value) {
                return pred.test(value) ? null : "Constraint violated on property value '" + value + "' (" + StringUtils.uncapitalize(constraintDescription) + ")";
            }


            @Override
            public String getConstraintDescription() {
                return StringUtils.capitalize(constraintDescription);
            }


            @Override
            public PropertyConstraint<Iterable<? extends U>> toCollectionConstraint() {
                final PropertyConstraint<U> thisValidator = this;
                return fromPredicate(
                    us -> {
                        for (U u : us) {
                            if (!pred.test(u)) {
                                return false;
                            }
                        }
                        return true;
                    },
                    "Components " + StringUtils.uncapitalize(thisValidator.getConstraintDescription())
                );
            }
        };
    }

}
