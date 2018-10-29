/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Validates the value of a property.
 *
 * @param <T> Type of value to handle
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
interface PropertyValidator<T> {

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
    String validate(T value);


    /**
     * Returns a description of the constraint
     * imposed by this validator on the values.
     * E.g. "The value should be positive", or
     * "The value should be one of A | B | C."
     *
     * @return A description of the constraint
     */
    String getConstraintDescription();


    // TODO Java 8 move PropertyFactory#fromPredicate here


}
