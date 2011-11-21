/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import java.util.List;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.dfa.JavaDataFlowNode;

public class JavaDataFlowHandler implements DataFlowHandler {
    public DataFlowNode createDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
	return new JavaDataFlowNode(dataFlow, node);
    }

    public Class<ASTLabeledStatement> getLabelStatementNodeClass() {
	return ASTLabeledStatement.class;
    }
}
