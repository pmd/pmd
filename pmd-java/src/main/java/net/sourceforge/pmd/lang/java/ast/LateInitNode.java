package net.sourceforge.pmd.lang.java.ast;

/**
 * Node that is not completely initialized at the point
 * {@link #jjtClose()} is called. This is the case when
 * additional children will be injected later-on.
 *
 * Only relevant to construction so it's package-private.
 * This is used before we have a rewrite phase to initialise
 * all nodes correctly. Otherwise their final structure doesn't
 * show in the designer.
 *
 * @author Clément Fournier
 * @since 1.2
 */
interface LateInitNode extends JavaNode {


    /**
     * Initialises this node after the additional children have been injected.
     * We assume this is only ever called once per node, which must be taken
     * care of by the injection logic in the parser.
     */
    void onInjectFinished();


}
