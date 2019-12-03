/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * An interface for querying information about interrelations of different AST nodes
 * from the minimizer point of view.
 */
public interface NodeInformationProvider {
    /**
     * Get all nodes the passed node directly depends on or <code>null</code> if don't know.
     *
     * Please note that returning empty set means "I do know: it doesn't depend on anything"!
     */
    Set<Node> getDirectDependencies(Node node);

    /**
     * Get all nodes that directly depend on the passed node or <code>null</code> if don't know.
     *
     * Please note that returning empty set means "I do know: nothing depends on it"!
     */
    Set<Node> getDirectlyDependingNodes(Node node);
}
