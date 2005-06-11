package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Each data flow contains a set of DataFlowNodes.
 */
public class DataFlowNode implements IDataFlowNode {

    private SimpleNode node;

    protected List parents = new ArrayList();
    protected List children = new ArrayList();
    protected BitSet type = new BitSet();
    protected LinkedList dataFlow;
    protected List variableAccess;
    protected int line;

    protected DataFlowNode() {}

    public DataFlowNode(SimpleNode node, LinkedList dataFlow) {
        this.dataFlow = dataFlow;
        this.node = node;

        node.setDataFlowNode(this);
        this.line = node.getBeginLine();

        if (!this.dataFlow.isEmpty()) {
            DataFlowNode parent = (DataFlowNode) this.dataFlow.getLast();
            parent.addPathToChild(this);
        }
        this.dataFlow.addLast(this);
    }

    public void addPathToChild(IDataFlowNode child) {
        DataFlowNode thisChild = (DataFlowNode) child;
        // TODO - throw an exception if already contained in children list?
        if (!this.children.contains(thisChild) || this.equals(thisChild)) {
            this.children.add(thisChild);
            thisChild.parents.add(this);
        }
    }

    public boolean removePathToChild(IDataFlowNode child) {
        DataFlowNode thisChild = (DataFlowNode) child;
        thisChild.parents.remove(this);
        return this.children.remove(thisChild);
    }

    public void reverseParentPathsTo(IDataFlowNode destination) {
        while (!parents.isEmpty()) {
            DataFlowNode parent = (DataFlowNode) parents.get(0);
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

    public boolean isType(int type) {
        try {
            return this.type.get(type);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public SimpleNode getSimpleNode() {
        return this.node;
    }

    public List getChildren() {
        return this.children;
    }

    public List getParents() {
        return this.parents;
    }

    public List getFlow() {
        return this.dataFlow;
    }

    public int getIndex() {
        return this.dataFlow.indexOf(this);
    }

    public void setVariableAccess(List variableAccess) {
        if (this.variableAccess == null) {
            this.variableAccess = variableAccess;
        } else {
            this.variableAccess.addAll(variableAccess);
        }
    }

    public List getVariableAccess() {
        return this.variableAccess;
    }

    public String toString() {
        String res = "DataFlowNode ";
        if (!isEmptyBitSet(type)) {
            String tmp = type.toString();
            String newTmp = "";
            for (int i=0; i<tmp.length(); i++) {
                if (tmp.charAt(i) != '{' && tmp.charAt(i) != '}') {
                    newTmp += tmp.charAt(i);
                }
            }
            res += "(" + newTmp + ")";
        } else {
            res += "(no type)";
        }
        res += ": " + this.node.getClass().toString();
        res += (node.getImage() == null ? "" : "(" + this.node.getImage() + ")");
        return res;
    }

    protected static final BitSet EMPTY_BITSET = new BitSet();
    protected static boolean isEmptyBitSet(BitSet bitSet) {
        // When we go to JDK 1.4, clean house
        //return bitSet.isEmpty();
        return bitSet.equals(EMPTY_BITSET);
    }
}
