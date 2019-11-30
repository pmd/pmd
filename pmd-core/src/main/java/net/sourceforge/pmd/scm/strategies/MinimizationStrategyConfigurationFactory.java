/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

public interface MinimizationStrategyConfigurationFactory {
    String getName();

    MinimizationStrategyConfiguration createConfiguration();
}
