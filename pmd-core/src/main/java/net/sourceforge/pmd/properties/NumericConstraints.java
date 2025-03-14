/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;


import static net.sourceforge.pmd.util.CollectionUtil.mapOf;

import net.sourceforge.pmd.util.internal.xml.SchemaConstants;

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
    public static <N extends Comparable<N>> PropertyConstraint<N> inRange(final N minInclusive, final N maxInclusive) {
        return PropertyConstraint.fromPredicate(
            t -> minInclusive.compareTo(t) <= 0 && maxInclusive.compareTo(t) >= 0,
            "Should be between " + minInclusive + " and " + maxInclusive,
                mapOf(SchemaConstants.PROPERTY_MIN.xmlName(), String.valueOf(minInclusive),
                        SchemaConstants.PROPERTY_MAX.xmlName(), String.valueOf(maxInclusive))
        );

    }

    /**
     * Requires the number to be greater than a lower bound.
     *
     * @param <N> Type of number
     *
     * @return A range constraint
     */
    public static <N extends Comparable<N>> PropertyConstraint<N> above(final N minInclusive) {
        return PropertyConstraint.fromPredicate(
            t -> minInclusive.compareTo(t) <= 0,
            "Should be greater or equal to " + minInclusive,
                mapOf(SchemaConstants.PROPERTY_MIN.xmlName(), String.valueOf(minInclusive))
        );
    }

    /**
     * Requires the number to be lower than an upper bound.
     *
     * @param <N> Type of number
     *
     * @return A range constraint
     */
    public static <N extends Comparable<N>> PropertyConstraint<N> below(final N maxInclusive) {
        return PropertyConstraint.fromPredicate(
            t -> maxInclusive.compareTo(t) >= 0,
            "Should be smaller or equal to " + maxInclusive,
                mapOf(SchemaConstants.PROPERTY_MAX.xmlName(), String.valueOf(maxInclusive))
        );
    }


    /**
     * Requires the number to be strictly positive.
     * The int values of the number is used for comparison
     * so there may be some unexpected behaviour with decimal
     * numbers.
     *
     * <p>Note: This constraint cannot be expressed in a XML ruleset.</p>
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
