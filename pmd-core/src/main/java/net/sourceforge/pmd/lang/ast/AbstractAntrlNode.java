package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

public abstract class AbstractAntrlNode implements AntlrNode {

    protected Node parent;

    private DataFlowNode dataFlowNode;
    private Object userData;

    @Override
    public abstract int getBeginLine();

    @Override
    public abstract int getBeginColumn();

    @Override
    public abstract int getEndLine();

    @Override
    public abstract int getEndColumn();

    @Override
    public DataFlowNode getDataFlowNode() {
        if (this.dataFlowNode == null) {
            if (this.parent != null) {
                return parent.getDataFlowNode();
            }
            return null; // TODO wise?
        }
        return dataFlowNode;
    }

    @Override
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    // TODO: should we make it abstract due to the comment in AbstractNode ?
    @Override
    public String getXPathNodeName() {
        return toString();
    }

    @Override
    public abstract Iterator<Attribute> getXPathAttributesIterator();
}
