package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;

public interface IDataFlowNode {
    List getVariableAccess();

    int getLine();

    int getIndex();

    boolean isType(int type);

    List getChildren();

    List getParents();

    List getFlow();

    SimpleNode getSimpleNode();

    void setVariableAccess(List variableAccess);

    void addPathToChild(IDataFlowNode child);

    boolean removePathToChild(IDataFlowNode child);

    void reverseParentPathsTo(IDataFlowNode destination);

}
