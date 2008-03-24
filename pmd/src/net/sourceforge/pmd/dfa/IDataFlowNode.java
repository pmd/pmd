/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;


import java.util.List;

public interface IDataFlowNode {
    List<VariableAccess> getVariableAccess();

    int getLine();

    int getIndex();

    boolean isType(int type);

    List<? extends IDataFlowNode> getChildren();

    List<? extends IDataFlowNode> getParents();

    List<? extends IDataFlowNode> getFlow();

    SimpleNode getSimpleNode();

    void setVariableAccess(List<VariableAccess> variableAccess);

    void addPathToChild(IDataFlowNode child);

    boolean removePathToChild(IDataFlowNode child);

    void reverseParentPathsTo(IDataFlowNode destination);

}
