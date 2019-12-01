/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
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

    /**
     * Get all nodes the passed one directly depends on or <code>null</code> if don't know.
     *
     * Please note that returning empty set means "I do know: it doesn't depend on anything"!
     */
    Set<Node> getDirectlyDependencies(Node node);

    /**
     * Get all nodes that directly depend on the passed one or <code>null</code> if don't know.
     *
     * Please note that returning empty set means "I do know: nothing depends on it"!
     */
    Set<Node> getDirectlyDependingNodes(Node node);
}
