/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Interface for starting an implementation of the visitors for ASTs.
 * 
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface VisitorStarter {

    /**
     * Placeholder {@link VisitorStarter} implementation that can be used when
     * no real implementation exists yet. This dummy implementation does
     * nothing.
     */
    VisitorStarter DUMMY = new VisitorStarter() {
        public void start(Node rootNode) {
            // does nothing - dummy implementation.
        }
    };

    /**
     * Start the visitor, given the root-node of the AST.
     * 
     * @param rootNode
     *            The root node of the AST
     */
    void start(Node rootNode);
}
