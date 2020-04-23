/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Base class for inner nodes.
 *
 * @param <I> Type of inner nodes of the language
 * @param <P> Supertype of all nodes of the language
 */
public abstract class AntlrBaseInnerNode<
    I extends AntlrBaseInnerNode<I, P>,
    P extends AntlrNode> extends ParserRuleContext implements AntlrNode {

    private final DataMap<DataKey<?, ?>> userData = DataMap.newDataMap();

    private int idxInParent = -1;

    /**
     * Constructor required by {@link ParserRuleContext}
     */
    protected AntlrBaseInnerNode() {
        // Nothing to be done
    }

    /**
     * Constructor required by {@link ParserRuleContext}
     *
     * @param parent The parent
     * @param invokingStateNumber the invokingState defined by {@link org.antlr.v4.runtime.RuleContext} parent
     */
    protected AntlrBaseInnerNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    /**
     * TODO @NoAttribute (port swift rules)
     */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public String getText() {
        return super.getText();
    }

    @Override
    public RuleContext addChild(RuleContext ruleInvocation) {
        return super.addChild(ruleInvocation);
    }

    protected void addOnlyChild(ParseTree child, Token first, Token last) {
        I cast = castToInnerNode(child);
        assert this.children == null;
        addChild(cast);
        cast.setParent(this);
        this.start = first;
        this.stop = last;
    }

    @Override
    public <T extends ParseTree> T addAnyChild(T t) {
        int numChildren = getNumChildren();
        if (t instanceof AntlrBaseInnerNode) {
            ((AntlrBaseInnerNode<?, ?>) t).setIndexInParent(numChildren);
        } else if (t instanceof PmdAntlrTerminalNode) {
            ((PmdAntlrTerminalNode) t).setIndexInParent(numChildren);
        }
        return super.addAnyChild(t);
    }

    void setIndexInParent(int idxInParent) {
        this.idxInParent = idxInParent;
    }

    @Override
    public int getIndexInParent() {
        return idxInParent;
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
    public DataMap<DataKey<?, ?>> getUserMap() {
        return userData;
    }

    @Override
    public P getChild(int i) {
        // this could be an error node, or a terminal
        return castToItf(super.getChild(i));
    }

    @Override
    public I getParent() {
        return castToInnerNode(super.getParent());
    }

    protected abstract P castToItf(ParseTree o);

    protected abstract I castToInnerNode(ParseTree o);

    @Override
    public int getNumChildren() {
        return getChildCount();
    }

    @Override
    public String getXPathNodeName() {
        final String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Context".length());
    }
}
