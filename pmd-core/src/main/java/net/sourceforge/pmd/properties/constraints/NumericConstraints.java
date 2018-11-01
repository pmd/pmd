/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.constraints;


import net.sourceforge.pmd.properties.constraints.ConstraintFactory.Predicate;


/**
 * Common constraints for properties dealing with numbers.
 *
 * @author Clément Fournier
 * @see PropertyConstraint
 * @since 6.10.0
 */
public class NumericConstraints {

    private NumericConstraints() {

    }

    // Methods are named as adjectives to mix well with the "require" syntax.


    /**
     * Requires the number to be inside a range.
     * The int values of the numbers are used so there
     * may be some unexpected behaviour with decimal numbers.
     *
     * @param <N> Type of number
     *
     * @return A range constraint
     */
    public static <N extends Number> PropertyConstraint<N> inRange(final N minInclusive, final N maxInclusive) {
        return ConstraintFactory.fromPredicate(new Predicate<N>() {
                                                   @Override
                                                   public boolean test(N t) {
                                                       return minInclusive.intValue() <= t.intValue() && maxInclusive.intValue() >= t.intValue();
                                                   }
                                               },
                                               "Should be between " + minInclusive + " and " + maxInclusive
        );

    }


    /**
     * Requires the number to be strictly positive.
     *
     * @param <N> Type of number
     *
     * @return A positivity constraint
     */
    public static <N extends Number> PropertyConstraint<N> positive() {
        return ConstraintFactory.fromPredicate(new Predicate<N>() {
                                                   @Override
                                                   public boolean test(N t) {
                                                       return t.intValue() > 0;
                                                   }
                                               },
                                               "Should be positive"
        );

    }
}
