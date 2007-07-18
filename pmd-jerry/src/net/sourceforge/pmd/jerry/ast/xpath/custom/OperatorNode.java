package net.sourceforge.pmd.jerry.ast.xpath.custom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.xpath.OperatorEnum;

public class OperatorNode extends SimpleNode {

	private List<OperatorEnum> operators;

	public OperatorNode(int id) {
		super(id);
	}

	public OperatorNode(XPath2Parser p, int id) {
		super(p, id);
	}

	public void addOperator(OperatorEnum operator) {
		if (operators == null) {
			operators = new ArrayList<OperatorEnum>();
		}
		operators.add(operator);
	}

	public int getNumOperators() {
		return operators != null ? operators.size() : 0;
	}

	public OperatorEnum getOperator(int i) {
		return operators.get(i);
	}
}
