package net.sourceforge.pmd.lang;

import java.util.LinkedList;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

public interface DataFlowHandler {

    DataFlowHandler DUMMY = new DataFlowHandler() {
	public DataFlowNode createDataFlowNode(LinkedList<DataFlowNode> dataFlow, Node node) {
	    return null;
	}
	public Class<? extends Node> getLabelStatementNodeClass() {
	    return null;
	}
    };

    DataFlowNode createDataFlowNode(LinkedList<DataFlowNode> dataFlow, Node node);
    Class<? extends Node> getLabelStatementNodeClass();
}
