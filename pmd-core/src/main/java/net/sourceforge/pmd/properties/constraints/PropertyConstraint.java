/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;

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
    // TODO Java 8 extend Predicate<T>


    boolean test(T value);


    /**
     * Returns a diagnostic message if the value
     * has a problem. Otherwise returns an empty
     * optional.
     *
     * @param value The value to validate
     *
     * @return An optional diagnostic message
     */
    // TODO Java 8 use Optional
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
     * Returns a constraint that validates a collection of Ts
     * by checking each component conforms to this conforms.
     *
     * @return A collection validator
     */
    @Experimental
    PropertyConstraint<Iterable<? extends T>> toCollectionConstraint();

    // TODO Java 8 move ConstraintFactory#fromPredicate here


}
