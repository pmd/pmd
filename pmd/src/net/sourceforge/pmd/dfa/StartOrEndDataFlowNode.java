/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dfa;

import java.util.LinkedList;

public class StartOrEndDataFlowNode extends AbstractDataFlowNode {

    private boolean isStartNode;

    public StartOrEndDataFlowNode(LinkedList<DataFlowNode> dataFlow, int line, boolean isStartNode) {
	super(dataFlow);
	this.line = line;
	this.isStartNode = isStartNode;
    }

    public String toString() {
	return isStartNode ? "Start node" : "End node";
    }
}
