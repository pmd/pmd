package net.sourceforge.pmd.lang.java.ast;

/**
 * Node that is not completely initialized at the point
 * {@link #jjtClose()} is called. This is the case when
 * additional children will be injected later-on.
 *
 * This only concerns some nodes produced in PrimarySuffix.
 *
 * Only relevant to construction so it's package-private.
 * That's also why it's an abstract class and not an
 * interface, otherwise the init method would need to be
 * public.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractLateInitNode extends AbstractJavaTypeNode {


    AbstractLateInitNode(int i) {
        super(i);
    }


    AbstractLateInitNode(JavaParser p, int i) {
        super(p, i);
    }


    /**
     * Initialises this node after the additional children have been injected.
     * We assume this is only ever called once per node, which must be taken
     * care of by the injection logic in the parser.
     */
    abstract void onInjectFinished();

}
