package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Each data flow contains a set of DataFlowNode's.
 */
public class DataFlowNode implements IDataFlowNode {

    private List parents;
    private List children;
    private BitSet type;
    private LinkedList dataFlow;
    private SimpleNode node;
    private List variableAccess;
    private int line;

    public DataFlowNode(LinkedList dataFlow, int line) {
        this(null, dataFlow);
        this.line = line;
    }

    public DataFlowNode(SimpleNode node, LinkedList dataFlow) {
        this.parents = new ArrayList();
        this.children = new ArrayList();
        this.dataFlow = dataFlow;
        this.node = node;
        this.type = new BitSet();

        if (this.node != null) {
            node.setDataFlowNode(this);
            this.line = node.getBeginLine();
        }
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
        if (node == null) {
            return res + "(SimpleNode is null)";
        }
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

    // TODO Remove when minimal runtime support is >= JDK 1.4
    private static final BitSet EMPTY_BITSET = new BitSet();
    private static boolean isEmptyBitSet(BitSet bitSet) {
        boolean empty = false;
        try {
            // Compatible with >= JDK 1.4
            if (BitSet.class.getMethod("isEmpty", null) != null) {
				    empty = bitSet.isEmpty();
            }
        } catch (NoSuchMethodException nsme) {
            // Compatible with < JDK 1.4
            empty = bitSet.equals(EMPTY_BITSET);
        }
        return empty;
    }
}
