package net.sourceforge.pmd.ast;

public class SimpleJavaAccessTypeNode extends AccessNode implements TypeNode {

	public SimpleJavaAccessTypeNode(int i) {
		super(i);
	}

	public SimpleJavaAccessTypeNode(JavaParser parser, int i) {
		super(parser, i);
	}

	private Class<?> type;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
