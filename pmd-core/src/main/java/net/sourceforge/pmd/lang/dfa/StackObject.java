/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa;

public class StackObject {

    private NodeType type;
    private DataFlowNode node;

    public StackObject(NodeType type, DataFlowNode node) {
        this.type = type;
        this.node = node;
    }

    public DataFlowNode getDataFlowNode() {
        return this.node;
    }

    public NodeType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "StackObject: type=" + type + ", node=" + node.toString();
    }
}
