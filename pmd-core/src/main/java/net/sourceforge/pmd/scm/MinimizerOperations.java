/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.Collection;

import net.sourceforge.pmd.lang.ast.Node;

public interface MinimizerOperations {
    /**
     * Get current language implementation
     */
    Language getLanguage();

    /**
     * Trim the specified nodes with all their descendants
     */
    void tryRemoveNodes(Collection<Node> nodesToRemove) throws Exception;

    void forceRemoveNodesAndExit(Collection<Node> nodesToRemove) throws Exception;

    /**
     * Get the parsed root node of the <b>input</b> file specified on the command line
     */
    Node getOriginalRoot();
}
