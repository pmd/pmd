/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.validators;

import net.sourceforge.pmd.annotation.Experimental;


/**
 * Validates the value of a property.
 *
 * @param <T> Type of value to handle
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
@Experimental
public interface PropertyValidator<T> {
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
     * E.g. "Should be positive", or
     * "Should be one of A | B | C."
     *
     * @return A description of the constraint
     */
    String getConstraintDescription();


    PropertyValidator<Iterable<? extends T>> toMulti();

    // TODO Java 8 move PropertyFactory#fromPredicate here


}
