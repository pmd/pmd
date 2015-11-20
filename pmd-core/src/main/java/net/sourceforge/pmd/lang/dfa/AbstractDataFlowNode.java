/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author raik
 *         <p/>
 *         Each data flow contains a set of DataFlowNodes.
 */
public abstract class AbstractDataFlowNode implements DataFlowNode {

    protected Node node;

    protected List<DataFlowNode> parents = new ArrayList<>();
    protected List<DataFlowNode> children = new ArrayList<>();
    protected BitSet type = new BitSet();
    protected List<VariableAccess> variableAccess = new ArrayList<>();
    protected List<DataFlowNode> dataFlow;
    protected int line;

    public AbstractDataFlowNode(List<DataFlowNode> dataFlow) {
	this.dataFlow = dataFlow;
	if (!this.dataFlow.isEmpty()) {
	    DataFlowNode parent = this.dataFlow.get(this.dataFlow.size() - 1);
	    parent.addPathToChild(this);
	}
	this.dataFlow.add(this);
    }

    public AbstractDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
	this(dataFlow);

	this.node = node;
	node.setDataFlowNode(this);
	this.line = node.getBeginLine();
    }

    public void addPathToChild(DataFlowNode child) {
	DataFlowNode thisChild = child;
	// TODO - throw an exception if already contained in children list?
	if (!this.children.contains(thisChild) || this.equals(thisChild)) {
	    this.children.add(thisChild);
	    thisChild.getParents().add(this);
	}
    }

    public boolean removePathToChild(DataFlowNode child) {
	DataFlowNode thisChild = child;
	thisChild.getParents().remove(this);
	return this.children.remove(thisChild);
    }

    public void reverseParentPathsTo(DataFlowNode destination) {
	while (!parents.isEmpty()) {
	    DataFlowNode parent = parents.get(0);
	    parent.removePathToChild(this);
	    parent.addPathToChild(destination);
	}
    }

    public int getLine() {
	return this.line;
    }

    public void setType(int type) {
	this.type.set(type);
    }

    public boolean isType(int intype) {
	try {
	    return type.get(intype);
	} catch (IndexOutOfBoundsException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public Node getNode() {
	return this.node;
    }

    public List<DataFlowNode> getChildren() {
	return this.children;
    }

    public List<DataFlowNode> getParents() {
	return this.parents;
    }

    public List<DataFlowNode> getFlow() {
	return this.dataFlow;
    }

    public int getIndex() {
	return this.dataFlow.indexOf(this);
    }

    public void setVariableAccess(List<VariableAccess> variableAccess) {
	if (this.variableAccess.isEmpty()) {
	    this.variableAccess = variableAccess;
	} else {
	    this.variableAccess.addAll(variableAccess);
	}
    }

    public List<VariableAccess> getVariableAccess() {
	return this.variableAccess;
    }

    @Override
    public String toString() {
	String res = "DataFlowNode: line " + this.getLine() + ", ";
	String tmp = type.toString();
	String newTmp = "";
	for (char c : tmp.toCharArray()) {
	    if (c != '{' && c != '}' && c != ' ') {
		newTmp += c;
	    }
	}
	for (StringTokenizer st = new StringTokenizer(newTmp, ","); st.hasMoreTokens();) {
	    int newTmpInt = Integer.parseInt(st.nextToken());
	    res += "(" + stringFromType(newTmpInt) + ")";
	}
	res += ", " + this.node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
	res += node.getImage() == null ? "" : "(" + this.node.getImage() + ")";
	return res;
    }

    private String stringFromType(int intype) {
	return NodeType.stringFromType(intype) ;
    }

}
