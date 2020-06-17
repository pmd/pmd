/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode.AntlrTerminalPmdAdapter;

/**
 * Base class for terminal nodes (they wrap a {@link TerminalNode}).
 */
public abstract class BaseAntlrTerminalNode<N extends AntlrNode<N>>
    extends BaseAntlrNode<AntlrTerminalPmdAdapter<N>, N> {

    private final AntlrTerminalPmdAdapter<N> antlrNode;

    protected BaseAntlrTerminalNode(Token symbol) {
        this(symbol, false);
    }

    BaseAntlrTerminalNode(Token symbol, boolean isError) {
        if (isError) {
            this.antlrNode = new AntlrErrorPmdAdapter<>(this, symbol);
        } else {
            this.antlrNode = new AntlrTerminalPmdAdapter<>(this, symbol);
        }
    }

    /**
     * Returns the text of the token.
     *
     * @implNote This should use {@link AntlrNameDictionary#getConstantImageOfToken(Token)},
     *     or default to {@link Token#getText()}
     */
    public abstract @NonNull String getText();

    @Override
    protected AntlrTerminalPmdAdapter<N> asAntlrNode() {
        return antlrNode;
    }

    @Override
    public Token getFirstAntlrToken() {
        return antlrNode.symbol;
    }

    @Override
    public Token getLastAntlrToken() {
        return antlrNode.symbol;
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    protected int getTokenKind() {
        return antlrNode.symbol.getTokenIndex();
    }

    @Override
    public N getChild(int index) {
        throw new IndexOutOfBoundsException("Index " + index + " for terminal node");
    }

    protected static class AntlrTerminalPmdAdapter<N extends AntlrNode<N>> extends TerminalNodeImpl implements AntlrToPmdParseTreeAdapter<N> {

        private final BaseAntlrTerminalNode<N> pmdNode;

        public AntlrTerminalPmdAdapter(BaseAntlrTerminalNode<N> pmdNode, Token symbol) {
            super(symbol);
            this.pmdNode = pmdNode;
        }

        @Override
        public AntlrToPmdParseTreeAdapter<N> getParent() {
            return (AntlrToPmdParseTreeAdapter<N>) super.getParent();
        }

        @Override
        public void setParent(RuleContext parent) {
            assert parent instanceof BaseAntlrNode.AntlrToPmdParseTreeAdapter;
            super.setParent(parent);
        }

        @Override
        public BaseAntlrNode<?, N> getPmdNode() {
            return pmdNode;
        }
    }

    protected static class AntlrErrorPmdAdapter<N extends AntlrNode<N>> extends AntlrTerminalPmdAdapter<N> implements ErrorNode {

        public AntlrErrorPmdAdapter(BaseAntlrTerminalNode<N> pmdNode, Token symbol) {
            super(pmdNode, symbol);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            return visitor.visitErrorNode(this);
        }
    }

}
