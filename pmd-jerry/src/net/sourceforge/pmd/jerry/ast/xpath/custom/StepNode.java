package net.sourceforge.pmd.jerry.ast.xpath.custom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.xpath.AxisEnum;

public class StepNode extends SimpleNode {

	private boolean root;

	private List<AxisEnum> axes;

	public StepNode(int id) {
		super(id);
	}

	public StepNode(XPath2Parser p, int id) {
		super(p, id);
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public void addAxis(AxisEnum axis) {
		if (axes == null) {
			axes = new ArrayList<AxisEnum>();
		}
		axes.add(axis);
	}

	public int getNumAxes() {
		return axes != null ? axes.size() : 0;
	}

	public AxisEnum getAxis(int i) {
		return axes.get(i);
	}
}