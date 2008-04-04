package net.sourceforge.pmd.lang.java.ast;

/**
 * This is a basic JavaNode implementation, useful when needing to create a
 * dummy node.
 */
public class DummyJavaNode extends AbstractJavaNode {

    public DummyJavaNode(int id) {
	super(id);
    }

    public DummyJavaNode(JavaParser parser, int id) {
	super(parser, id);
    }
}
