/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dfa;

public class StackObject {

    private int type;
    private IDataFlowNode node;

    protected StackObject(int type, IDataFlowNode node) {
        this.type = type;
        this.node = node;
    }

    public IDataFlowNode getDataFlowNode() {
        return this.node;
    }

    public int getType() {
        return this.type;
    }
}
