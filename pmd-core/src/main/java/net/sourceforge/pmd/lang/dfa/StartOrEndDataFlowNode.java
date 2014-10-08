/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.List;


public class StartOrEndDataFlowNode extends AbstractDataFlowNode {

    private boolean isStartNode;

    public StartOrEndDataFlowNode(List<DataFlowNode> dataFlow, int line, boolean isStartNode) {
	super(dataFlow);
	this.line = line;
	this.isStartNode = isStartNode;
    }

    public String toString() {
	return isStartNode ? "Start node" : "End node";
    }
}
