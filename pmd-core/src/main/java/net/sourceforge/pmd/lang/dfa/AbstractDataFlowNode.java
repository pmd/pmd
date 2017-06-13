/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Each data flow contains a set of DataFlowNodes.
 *
 * @author raik
 */
public abstract class AbstractDataFlowNode implements DataFlowNode {

    protected Node node;

    protected List<DataFlowNode> parents = new ArrayList<>();
    protected List<DataFlowNode> children = new ArrayList<>();
    protected Set<NodeType> type = EnumSet.noneOf(NodeType.class);
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

    @Override
    public void addPathToChild(DataFlowNode child) {
        DataFlowNode thisChild = child;
        // TODO - throw an exception if already contained in children list?
        if (!this.children.contains(thisChild) || this.equals(thisChild)) {
            this.children.add(thisChild);
            thisChild.getParents().add(this);
        }
    }

    @Override
    public boolean removePathToChild(DataFlowNode child) {
        DataFlowNode thisChild = child;
        thisChild.getParents().remove(this);
        return this.children.remove(thisChild);
    }

    @Override
    public void reverseParentPathsTo(DataFlowNode destination) {
        while (!parents.isEmpty()) {
            DataFlowNode parent = parents.get(0);
            parent.removePathToChild(this);
            parent.addPathToChild(destination);
        }
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public void setType(NodeType type) {
        this.type.add(type);
    }

    @Override
    public boolean isType(NodeType type) {
        return this.type.contains(type);
    }

    @Override
    public Node getNode() {
        return this.node;
    }

    @Override
    public List<DataFlowNode> getChildren() {
        return this.children;
    }

    @Override
    public List<DataFlowNode> getParents() {
        return this.parents;
    }

    @Override
    public List<DataFlowNode> getFlow() {
        return this.dataFlow;
    }

    @Override
    public int getIndex() {
        return this.dataFlow.indexOf(this);
    }

    @Override
    public void setVariableAccess(List<VariableAccess> variableAccess) {
        if (this.variableAccess.isEmpty()) {
            this.variableAccess = variableAccess;
        } else {
            this.variableAccess.addAll(variableAccess);
        }
    }

    @Override
    public List<VariableAccess> getVariableAccess() {
        return this.variableAccess;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataFlowNode: line ");
        sb.append(this.getLine());
        sb.append(", ");

        for (NodeType t : type) {
            sb.append("(");
            sb.append(t.toString());
            sb.append(")");
        }

        sb.append(", ");
        sb.append(this.node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1));
        sb.append(node.getImage() == null ? "" : "(" + this.node.getImage() + ")");
        return sb.toString();
    }

}
