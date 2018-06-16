/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.util.List;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.dfa.PLSQLDataFlowNode;

public class PLSQLDataFlowHandler implements DataFlowHandler {
    @Override
    public DataFlowNode createDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
        return new PLSQLDataFlowNode(dataFlow, node);
    }

    @Override
    public Class<ASTLabelledStatement> getLabelStatementNodeClass() {
        return ASTLabelledStatement.class;
    }
}
