/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

public interface DataFlowHandler {

    DataFlowHandler DUMMY = new DataFlowHandler() {
	public DataFlowNode createDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
	    return null;
	}
	public Class<? extends Node> getLabelStatementNodeClass() {
	    return null;
	}
    };

    DataFlowNode createDataFlowNode(List<DataFlowNode> dataFlow, Node node);
    Class<? extends Node> getLabelStatementNodeClass();
}
