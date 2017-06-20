/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Option for class metrics determining what they return. ResultOptions allow us to return the sum, or
 * average, or the highest value of the metric computed on the operations of a class. They help to reduce the
 * need for custom implementations of those calculations in rules. Thus, they greatly reduce the amount of code
 * required to produce detailed violation reports for classes.
 *
 * @author Clément Fournier
 */
public enum ResultOption implements MetricOption {
    DEFAULT, SUM, AVERAGE, HIGHEST
}
