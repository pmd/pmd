/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.scm.MinimizerOperations;

/**
 * What steps to perform to minimize the source.
 */
public interface MinimizationStrategy {
    enum PassResult {
        ROLLBACK_AND_EXIT,
        COMMIT_AND_EXIT,
        COMMIT_AND_CONTINUE,
    }

    /**
     * Called once before starting the minimization.
     *
     * @param rootNode Root node of the original source file
     */
    void initialize(Node rootNode);

    /**
     * Performs single minimization pass.
     */
    PassResult performSinglePass(MinimizerOperations ops, Node currentRoot) throws Exception;
}
