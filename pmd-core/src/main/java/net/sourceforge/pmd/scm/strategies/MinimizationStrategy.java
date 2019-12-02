/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * What steps to perform to minimize the source.
 */
public interface MinimizationStrategy {
    /**
     * Called once before starting the minimization.
     *
     * @param rootNode Root node of the original source file
     */
    void initialize(MinimizerOperations ops, Node rootNode);

    /**
     * Performs single minimization pass.
     */
    void performSinglePass(Node currentRoot) throws Exception;
}
