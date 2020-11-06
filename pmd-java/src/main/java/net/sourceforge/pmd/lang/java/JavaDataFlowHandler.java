/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.dfa.JavaDataFlowNode;

/**
 * @deprecated Is internal API.
 */
@InternalApi
@Deprecated
public class JavaDataFlowHandler implements DataFlowHandler {
    @Override
    public DataFlowNode createDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
        return new JavaDataFlowNode(dataFlow, node);
    }

    @Override
    public Class<ASTLabeledStatement> getLabelStatementNodeClass() {
        return ASTLabeledStatement.class;
    }
}
