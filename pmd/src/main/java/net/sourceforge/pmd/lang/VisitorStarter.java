package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Interface for starting an implementation of the visitors for ASTs.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface VisitorStarter {

    VisitorStarter DUMMY = new VisitorStarter() {
	public void start(Node rootNode) {
	}
    };

    /**
     * Start the visitor, given the root-node of the AST.
     *
     * @param rootNode The root node of the AST
     */
    void start(Node rootNode);
}
