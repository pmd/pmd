/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Checks some invariant about processing the source by the compiler
 */
public interface Invariant {
    /**
     * Called once before starting the minimization.
     *
     * @param rootNode Root node of the original source file
     */
    void initialize(InvariantOperations ops, Node rootNode);

    boolean checkIsSatisfied() throws Exception;
}
