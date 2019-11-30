/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

/**
 * Parameters of the minimization strategy. To be specified on command line.
 *
 * @see com.beust.jcommander.Parameter
 */
public interface MinimizationStrategyConfiguration {
    MinimizationStrategy createStrategy();
}
