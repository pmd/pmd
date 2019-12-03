/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

/**
 * Parameters of the minimization strategy. To be specified on command line.
 *
 * @see com.beust.jcommander.JCommander
 * @see com.beust.jcommander.Parameter
 */
public interface MinimizationStrategyConfiguration {
    /**
     * Creates minimization strategy instance according to the current state of this object.
     *
     * Future changes to this object do not affected the created strategy.
     */
    MinimizationStrategy createStrategy();
}
