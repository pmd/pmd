/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import java.util.Collection;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.scm.NodeInformationProvider;

/**
 * A public interface provided be {@link net.sourceforge.pmd.scm.SourceCodeMinimizer} to
 * {@link MinimizationStrategy}.
 */
public interface MinimizerOperations {
    /**
     * Get object that can be queried for relations between nodes.
     */
    NodeInformationProvider getNodeInformationProvider();

    /**
     * Try cleaning up source code.
     *
     * <b>Tries</b> to not change the AST.
     * <b>Does not</b> commit broken invariants.
     */
    void tryCleanup() throws Exception;

    /**
     * Trim the specified nodes with all their descendants.
     */
    void tryRemoveNodes(Collection<Node> nodesToRemove) throws Exception;

    /**
     * Removes the specified nodes (even if producing source code that cannot be re-parsed), then exits.
     */
    void forceRemoveNodesAndExit(Collection<Node> nodesToRemove) throws Exception;

    /**
     * Get the parsed root node of the <b>input</b> file specified on the command line.
     */
    Node getOriginalRoot();
}
