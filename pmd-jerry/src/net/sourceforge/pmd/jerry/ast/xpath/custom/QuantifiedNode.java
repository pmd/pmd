package net.sourceforge.pmd.jerry.ast.xpath.custom;

import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;

public class QuantifiedNode extends SimpleNode {

	private boolean existential;

	public QuantifiedNode(int id) {
		super(id);
	}

	public QuantifiedNode(XPath2Parser p, int id) {
		super(p, id);
	}

	public boolean isExistential() {
		return existential;
	}

	public void setExistential(boolean existential) {
		this.existential = existential;
	}
}
