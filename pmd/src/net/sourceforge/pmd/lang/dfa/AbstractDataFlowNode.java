/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author raik
 *         <p/>
 *         Each data flow contains a set of DataFlowNodes.
 */
public abstract class AbstractDataFlowNode implements DataFlowNode {

    protected Node node;
    protected Map<Integer, String> typeMap = new HashMap<Integer, String>();

    protected List<DataFlowNode> parents = new ArrayList<DataFlowNode>();
    protected List<DataFlowNode> children = new ArrayList<DataFlowNode>();
    protected BitSet type = new BitSet();
    protected List<VariableAccess> variableAccess = new ArrayList<VariableAccess>();
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
	if (typeMap.isEmpty()) {
	    typeMap.put(NodeType.IF_EXPR, "IF_EXPR");
	    typeMap.put(NodeType.IF_LAST_STATEMENT, "IF_LAST_STATEMENT");
	    typeMap.put(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, "IF_LAST_STATEMENT_WITHOUT_ELSE");
	    typeMap.put(NodeType.ELSE_LAST_STATEMENT, "ELSE_LAST_STATEMENT");
	    typeMap.put(NodeType.WHILE_LAST_STATEMENT, "WHILE_LAST_STATEMENT");
	    typeMap.put(NodeType.WHILE_EXPR, "WHILE_EXPR");
	    typeMap.put(NodeType.SWITCH_START, "SWITCH_START");
	    typeMap.put(NodeType.CASE_LAST_STATEMENT, "CASE_LAST_STATEMENT");
	    typeMap.put(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, "SWITCH_LAST_DEFAULT_STATEMENT");
	    typeMap.put(NodeType.SWITCH_END, "SWITCH_END");
	    typeMap.put(NodeType.FOR_INIT, "FOR_INIT");
	    typeMap.put(NodeType.FOR_EXPR, "FOR_EXPR");
	    typeMap.put(NodeType.FOR_UPDATE, "FOR_UPDATE");
	    typeMap.put(NodeType.FOR_BEFORE_FIRST_STATEMENT, "FOR_BEFORE_FIRST_STATEMENT");
	    typeMap.put(NodeType.FOR_END, "FOR_END");
	    typeMap.put(NodeType.DO_BEFORE_FIRST_STATEMENT, "DO_BEFORE_FIRST_STATEMENT");
	    typeMap.put(NodeType.DO_EXPR, "DO_EXPR");
	    typeMap.put(NodeType.RETURN_STATEMENT, "RETURN_STATEMENT");
	    typeMap.put(NodeType.BREAK_STATEMENT, "BREAK_STATEMENT");
	    typeMap.put(NodeType.CONTINUE_STATEMENT, "CONTINUE_STATEMENT");
	    typeMap.put(NodeType.LABEL_STATEMENT, "LABEL_STATEMENT");
	    typeMap.put(NodeType.LABEL_LAST_STATEMENT, "LABEL_END");
	    typeMap.put(NodeType.THROW_STATEMENT, "THROW_STATEMENT");
	}
	if (!typeMap.containsKey(intype)) {
	    throw new RuntimeException("Couldn't find type id " + intype);
	}
	return typeMap.get(intype);
    }

}
