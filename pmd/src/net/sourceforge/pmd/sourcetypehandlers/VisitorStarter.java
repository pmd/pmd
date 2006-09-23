package net.sourceforge.pmd.sourcetypehandlers;

/**
 * Interface for starting an implementation of the parser visitors for
 * the grammars.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface VisitorStarter {

	VisitorStarter dummy = new VisitorStarter() { public void start(Object rootNode) {} };
	
    /**
     * Start the visitor, given the root-node of the AST.
     *
     * @param rootNode The root node of the AST
     */
    void start(Object rootNode);

}
