package net.sourceforge.pmd.jerry.ast.xpath.custom;

import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;

public class WildcardNode extends ImageNode {

	private boolean prefix;

	public WildcardNode(int id) {
		super(id);
	}

	public WildcardNode(XPath2Parser p, int id) {
		super(p, id);
	}

	public boolean isPrefix() {
		return prefix;
	}

	public void setPrefix(boolean prefix) {
		this.prefix = prefix;
	}
}
