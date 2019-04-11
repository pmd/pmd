/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;

public class AntlrBaseNode extends ParserRuleContext implements AntlrNode {

    // TODO: what should we do with parent? how do we handle data flows in this scenario? it's ok to ignore
    // TODO: our parent data flow in case we don't have one?
    // protected Node parent;

    private DataFlowNode dataFlowNode;
    private Object userData;

    /**
     * Constructor required by {@link ParserRuleContext}
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode() {
        // Nothing to be done
    }

    /**
     * Constructor required by {@link ParserRuleContext}
     *
     * @param parent The parent
     * @param invokingStateNumber the invokingState defined by {@link org.antlr.v4.runtime.RuleContext} parent
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public Node jjtGetParent() {
        return (Node) parent; // TODO: review if all parents are Nodes
    }

    @Override
    public int getBeginLine() {
        return start.getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        return stop.getLine(); // This goes from 1 to n
    }

    @Override
    public int getBeginColumn() {
        return start.getCharPositionInLine(); // This goes from 0 to (n - 1)
    }

    @Override
    public int getEndColumn() {
        return stop.getCharPositionInLine(); // This goes from 0 to (n - 1)
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

    @Override
    public Node jjtGetChild(final int index) {
        return (Node) children.get(index); // TODO: review if all children are Nodes
    }

    @Override
    public int jjtGetNumChildren() {
        return children == null ? 0 : children.size();
    }

    // TODO: should we make it abstract due to the comment in AbstractNode ?
    @Override
    public String getXPathNodeName() {
        return toString();
    }
}
