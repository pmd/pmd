/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AntlrBaseNode extends ParserRuleContext implements AntlrNode {

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

    // FIXME these coordinates are not accurate

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
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    @Override
    public AntlrNode getChild(int i) {
        return (AntlrNode) super.getChild(i);
    }

    @Override
    public AntlrBaseNode getParent() {
        return (AntlrBaseNode) super.getParent();
    }

    @Override
    public int getNumChildren() {
        return getChildCount();
    }

    // TODO: should we make it abstract due to the comment in AbstractNode ?
    @Override
    public String getXPathNodeName() {
        final String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Context".length());
    }
}
