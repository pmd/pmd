/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;


/**
 * Common constraints for properties dealing with numbers.
 *
 * @author Clément Fournier
 * @see PropertyConstraint
 * @since 6.10.0
 */
public final class NumericConstraints {

    private NumericConstraints() {

    }

    // Methods are named to mix well with the "require" syntax.


    /**
     * Requires the number to be inside a range.
     *
     * @param <N> Type of number
     *
     * @return A range constraint
     */
    public static <N extends Number & Comparable<N>> PropertyConstraint<N> inRange(final N minInclusive, final N maxInclusive) {
        return PropertyConstraint.fromPredicate(
            t -> minInclusive.compareTo(t) <= 0 && maxInclusive.compareTo(t) >= 0,
                "Should be between " + minInclusive + " and " + maxInclusive
        );

    }


    /**
     * Requires the number to be strictly positive.
     * The int values of the number is used for comparison
     * so there may be some unexpected behaviour with decimal
     * numbers.
     *
     * @param <N> Type of number
     *
     * @return A positivity constraint
     */
    public static <N extends Number> PropertyConstraint<N> positive() {
        return PropertyConstraint.fromPredicate(
            t -> t.intValue() > 0,
                "Should be positive"
        );
    }
}
