/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode.PmdAsAntlrInnerNode;

/**
 * Base class for the inner nodes (corresponds to {@link ParserRuleContext}).
 * Use the {@code contextSuperClass} option to set this in the antlr g4 file,
 * eg {@code options { contextSuperClass = SwiftInnerNode; }}.
 */
public abstract class BaseAntlrInnerNode<N extends AntlrNode<N>> extends BaseAntlrNode<PmdAsAntlrInnerNode<N>, N> {

    public RecognitionException exception;

    private final PmdAsAntlrInnerNode<N> antlrNode;

    protected BaseAntlrInnerNode() {
        antlrNode = new PmdAsAntlrInnerNode<>(this);
    }

    protected BaseAntlrInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        antlrNode = new PmdAsAntlrInnerNode<>(this, (PmdAsAntlrInnerNode<N>) parent, invokingStateNumber);
    }

    @Override
    @SuppressWarnings("unchecked")
    public N getChild(int index) {
        if (0 <= index && index < getNumChildren()) {
            N pmdNode = (N) antlrNode.getChild(index).getPmdNode();
            assert pmdNode.getIndexInParent() == index;
            return pmdNode;
        }
        throw new IndexOutOfBoundsException("Index " + index + ", numChildren " + getNumChildren());
    }

    @Override
    public int getNumChildren() {
        return antlrNode.getChildCount();
    }

    @Override
    protected PmdAsAntlrInnerNode<N> asAntlrNode() {
        return antlrNode;
    }

    protected abstract int getRuleIndex();


    @Override
    public Token getFirstAntlrToken() {
        return asAntlrNode().start;
    }

    @Override
    public Token getLastAntlrToken() {
        return asAntlrNode().stop;
    }

    protected <T extends BaseAntlrInnerNode<N>> T getRuleContext(Class<T> klass, int idx) {
        return children(klass).get(idx);
    }

    protected <T extends BaseAntlrInnerNode<N>> List<T> getRuleContexts(Class<T> klass) {
        return children(klass).toList();
    }

    protected TerminalNode getToken(int kind, int idx) {
        @SuppressWarnings("rawtypes")
        BaseAntlrTerminalNode pmdWrapper =
            children(BaseAntlrTerminalNode.class)
                .filter(it -> it.getTokenKind() == kind)
                .get(idx);
        return pmdWrapper != null ? pmdWrapper.asAntlrNode() : null;
    }

    protected void copyFrom(BaseAntlrInnerNode<N> other) {
        asAntlrNode().copyFrom(other.asAntlrNode());
    }


    public void enterRule(ParseTreeListener listener) {
        // default does nothing
    }


    public void exitRule(ParseTreeListener listener) {
        // default does nothing
    }

    protected static class PmdAsAntlrInnerNode<N extends AntlrNode<N>> extends ParserRuleContext implements RuleNode, AntlrToPmdParseTreeAdapter<N> {

        private final BaseAntlrInnerNode<N> pmdNode;

        PmdAsAntlrInnerNode(BaseAntlrInnerNode<N> node) {
            this.pmdNode = node;
        }

        PmdAsAntlrInnerNode(BaseAntlrInnerNode<N> node, PmdAsAntlrInnerNode<N> parent, int invokingStateNumber) {
            super(parent, invokingStateNumber);
            this.pmdNode = node;
        }

        @Override
        public BaseAntlrInnerNode<N> getPmdNode() {
            return pmdNode;
        }

        @Override
        @SuppressWarnings("unchecked")
        public PmdAsAntlrInnerNode<N> getParent() {
            return (PmdAsAntlrInnerNode<N>) super.getParent();
        }

        @Override
        @SuppressWarnings("unchecked")
        public AntlrToPmdParseTreeAdapter<N> getChild(int i) {
            return (AntlrToPmdParseTreeAdapter<N>) super.getChild(i);
        }

        @Override
        public <T extends ParseTree> T addAnyChild(T t) {
            assert t instanceof AntlrToPmdParseTreeAdapter;
            BaseAntlrNode<?, ?> pmdNode = ((AntlrToPmdParseTreeAdapter<?>) t).getPmdNode();
            pmdNode.setIndexInParent(getChildCount());
            return super.addAnyChild(t);
        }

        @Override
        public void setParent(RuleContext parent) {
            assert parent instanceof PmdAsAntlrInnerNode;
            super.setParent(parent);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            throw new UnsupportedOperationException("Cannot visit the underlying antlr nodes");
        }
    }
}
