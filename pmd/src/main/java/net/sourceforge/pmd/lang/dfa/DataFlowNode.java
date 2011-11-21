/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;

public interface DataFlowNode {
    List<VariableAccess> getVariableAccess();

    int getLine();

    int getIndex();

    boolean isType(int type);

    void setType(int type);

    List<DataFlowNode> getChildren();

    List<DataFlowNode> getParents();

    List<DataFlowNode> getFlow();

    Node getNode();

    void setVariableAccess(List<VariableAccess> variableAccess);

    void addPathToChild(DataFlowNode child);

    boolean removePathToChild(DataFlowNode child);

    void reverseParentPathsTo(DataFlowNode destination);

}
