package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import org.antlr.v4.runtime.ParserRuleContext;

public class AntrlBaseNode extends ParserRuleContext implements AntlrNode {

    // TODO: what should we do with parent? how do we handle data flows in this scenario? it's ok to ignore
    // TODO: our parent data flow in case we don't have one?
    // protected Node parent;

    private DataFlowNode dataFlowNode;
    private Object userData;

    @Override
    public int getBeginLine() {
        return start.getLine();
    }

    @Override
    public int getBeginColumn() {
        return start.getCharPositionInLine();
    }

    @Override
    public int getEndLine() {
        return stop.getLine();
    }

    @Override
    public int getEndColumn() {
        return stop.getCharPositionInLine();
    }

    @Override
    public DataFlowNode getDataFlowNode() {
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
    public Iterator<Attribute> getXPathAttributesIterator() {
        return new AttributeAxisIterator(this);
    }
}
