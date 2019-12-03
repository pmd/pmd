/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.List;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.scm.invariants.InvariantConfiguration;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfiguration;

/**
 * An entry point for the specific language module for Source Code Minimizer.
 */
public interface Language {
    /**
     * Get the terse language name for use on the command line.
     */
    String getTerseName();

    /**
     * Get the supported version names of this language implementation.
     */
    List<String> getLanguageVersions();

    /**
     * Get the language version to be used by default.
     */
    String getDefaultLanguageVersion();

    /**
     * Creates parser for this language for the specified version.
     */
    Parser getParser(String languageVersion);

    /**
     * Get minimization strategy identifiers for use on command line, either generic or language-specific.
     */
    List<String> getStrategyNames();

    /**
     * Get strategy configuration factory by its identifier.
     */
    MinimizationStrategyConfiguration createStrategyConfiguration(String name);

    /**
     * Get invariant identifiers for use on command line, either generic or language-specific.
     */
    List<String> getInvariantNames();

    /**
     * Get invariant configuration factory by its identifier.
     */
    InvariantConfiguration createInvariantConfiguration(String name);

    NodeInformationProvider getNodeInformationProvider();
}
