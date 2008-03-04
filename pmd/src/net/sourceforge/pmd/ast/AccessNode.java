package net.sourceforge.pmd.ast;

//FUTURE Delete this class, current usages need to change to the AccessNode interface, or SimpleJavaAccessNode as appropriate.

/**
 * @deprecated Use SimpleJavaAccessNode instead.
 */
@Deprecated
public class AccessNode extends SimpleJavaAccessNode {

	public AccessNode(int i) {
		super(i);
	}

	public AccessNode(JavaParser parser, int i) {
		super(parser, i);
	}
}
