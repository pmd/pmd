package net.sourceforge.pmd.dfa;

public class StackObject {

    private int type;
    private IDataFlowNode node;

    protected StackObject(int type, IDataFlowNode node) {
        this.setType(type);
        this.setDataFlowNode(node);
    }

    public IDataFlowNode getDataFlowNode() {
        return this.node;
    }

    public int getType() {
        return this.type;
    }

    protected void setDataFlowNode(IDataFlowNode node) {
        this.node = node;
    }

    protected void setType(int type) {
        this.type = type;
    }

}
