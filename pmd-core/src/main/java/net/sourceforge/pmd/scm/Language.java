/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.List;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.scm.invariants.InvariantConfiguration;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfiguration;

public interface Language {
    /**
     * Get the terse language name for use in configuration
     */
    String getTerseName();

    /**
     * Get parser for this language
     */
    Parser getParser();

    List<String> getStrategyNames();

    MinimizationStrategyConfiguration createStrategyConfiguration(String name);

    List<String> getInvariantNames();

    InvariantConfiguration createInvariantConfiguration(String name);
}
