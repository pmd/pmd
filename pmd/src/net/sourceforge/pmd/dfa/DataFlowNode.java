package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author raik
 *         <p/>
 *         Each data flow contains a set of DataFlowNodes.
 */
public class DataFlowNode implements IDataFlowNode {

    private SimpleNode node;
    private Map typeMap = new HashMap();

    protected List parents = new ArrayList();
    protected List children = new ArrayList();
    protected BitSet type = new BitSet();
    protected List variableAccess = new ArrayList();
    protected LinkedList dataFlow;
    protected int line;

    protected DataFlowNode() {
    }

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

    public boolean isType(int intype) {
        try {
            return type.get(intype);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
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
        if (this.variableAccess.isEmpty()) {
            this.variableAccess = variableAccess;
        } else {
            this.variableAccess.addAll(variableAccess);
        }
    }

    public List getVariableAccess() {
        return this.variableAccess;
    }

    public String toString() {
        String res = "DataFlowNode: line " + this.getLine() + ", ";
        if (node instanceof ASTMethodDeclaration || node instanceof ASTConstructorDeclaration) {
            res += (node instanceof ASTMethodDeclaration) ? "(method)" : "(constructor)";
        } else {
            String tmp = type.toString();
            String newTmp = "";
            for (int i = 0; i < tmp.length(); i++) {
                if (tmp.charAt(i) != '{' && tmp.charAt(i) != '}' && tmp.charAt(i) != ' ') {
                    newTmp += tmp.charAt(i);
                }
            }
            for (StringTokenizer st = new StringTokenizer(newTmp, ","); st.hasMoreTokens();) {
                int newTmpInt = Integer.parseInt(st.nextToken());
                res += "(" + stringFromType(newTmpInt) + ")";
            }
            res += ", " + this.node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
            res += (node.getImage() == null ? "" : "(" + this.node.getImage() + ")");
        }
        return res;
    }

    private String stringFromType(int intype) {
        if (typeMap.isEmpty()) {
            typeMap.put(new Integer(NodeType.IF_EXPR), "IF_EXPR");
            typeMap.put(new Integer(NodeType.IF_LAST_STATEMENT), "IF_LAST_STATEMENT");
            typeMap.put(new Integer(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE), "IF_LAST_STATEMENT_WITHOUT_ELSE");
            typeMap.put(new Integer(NodeType.ELSE_LAST_STATEMENT), "ELSE_LAST_STATEMENT");
            typeMap.put(new Integer(NodeType.WHILE_LAST_STATEMENT), "WHILE_LAST_STATEMENT");
            typeMap.put(new Integer(NodeType.WHILE_EXPR), "WHILE_EXPR");
            typeMap.put(new Integer(NodeType.SWITCH_START), "SWITCH_START");
            typeMap.put(new Integer(NodeType.CASE_LAST_STATEMENT), "CASE_LAST_STATEMENT");
            typeMap.put(new Integer(NodeType.SWITCH_LAST_DEFAULT_STATEMENT), "SWITCH_LAST_DEFAULT_STATEMENT");
            typeMap.put(new Integer(NodeType.SWITCH_END), "SWITCH_END");
            typeMap.put(new Integer(NodeType.FOR_INIT), "FOR_INIT");
            typeMap.put(new Integer(NodeType.FOR_EXPR), "FOR_EXPR");
            typeMap.put(new Integer(NodeType.FOR_UPDATE), "FOR_UPDATE");
            typeMap.put(new Integer(NodeType.FOR_BEFORE_FIRST_STATEMENT), "FOR_BEFORE_FIRST_STATEMENT");
            typeMap.put(new Integer(NodeType.FOR_END), "FOR_END");
            typeMap.put(new Integer(NodeType.DO_BEFORE_FIRST_STATEMENT), "DO_BEFORE_FIRST_STATEMENT");
            typeMap.put(new Integer(NodeType.DO_EXPR), "DO_EXPR");
            typeMap.put(new Integer(NodeType.RETURN_STATEMENT), "RETURN_STATEMENT");
            typeMap.put(new Integer(NodeType.BREAK_STATEMENT), "BREAK_STATEMENT");
            typeMap.put(new Integer(NodeType.CONTINUE_STATEMENT), "CONTINUE_STATEMENT");
            typeMap.put(new Integer(NodeType.LABEL_STATEMENT), "LABEL_STATEMENT");
            typeMap.put(new Integer(NodeType.LABEL_LAST_STATEMENT), "LABEL_END");
        }
        if (!typeMap.containsKey(new Integer(intype))) {
            throw new RuntimeException("Couldn't find type id " + intype);
        }
        return (String) typeMap.get(new Integer(intype));
    }

}
