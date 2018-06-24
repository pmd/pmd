/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

/**
 * Options to calculate a result aggregated on the operations of a class. ResultOptions allow us to return the sum, or
 * average, or the highest value of the metric computed on the operations of a class. They help to reduce the need for
 * custom implementations of those calculations in rules. Thus, they greatly reduce the amount of code required to
 * produce detailed violation reports for classes.
 *
 * @author Clément Fournier
 * @since 5.8.0
 */
public enum ResultOption {
    /** Compute the sum on all operations. */
    SUM,
    /** Compute the average on all operations. */
    AVERAGE,
    /** Compute the highest value among all operations. */
    HIGHEST
}
