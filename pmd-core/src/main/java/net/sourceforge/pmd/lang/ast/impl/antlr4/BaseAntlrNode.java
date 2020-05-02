/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrNode.AntlrToPmdParseTreeAdapter;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Base class for the parser rule contexts, use {@code contextSuperClass} option
 * in the antlr grammar.
 */
public abstract class BaseAntlrNode<A extends AntlrToPmdParseTreeAdapter<N>, N extends GenericNode<N>> implements GenericNode<N> {

    private DataMap<DataKey<?, ?>> userMap;
    private int indexInParent = -1;

    protected BaseAntlrNode() {
        // protected
    }

    /**
     * Recurses over the text of all terminal descendants to build the
     * text of this node (without spaces). This is extremely inefficient
     * and should not be used to write rules. The antlr impl doesn't even
     * use a single stringbuilder.
     *
     * @deprecated Some rules depend on it and have not been rewritten
     */
    @Deprecated
    public String joinTokenText() {
        return asAntlrNode().getText();
    }

    // these are an implementation detail, meant as a crutch while some
    // rules depend on it
    // Should be made protected

    public abstract Token getFirstAntlrToken();

    public abstract Token getLastAntlrToken();

    @Override
    public N getParent() {
        return (N) asAntlrNode().getParent().getPmdNode();
    }

    @Override
    public int getBeginLine() {
        return getFirstAntlrToken().getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return getLastAntlrToken().getLine();
    }

    @Override
    public int getBeginColumn() {
        return getFirstAntlrToken().getCharPositionInLine() + 1;
    }

    @Override
    public int getEndColumn() {
        Token tok = getLastAntlrToken();
        return tok.getCharPositionInLine() + tok.getStopIndex() - tok.getStartIndex() + 1;
    }

    @Override
    public int getIndexInParent() {
        assert indexInParent >= 0 : "Index not set";
        return indexInParent;
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        if (userMap == null) {
            userMap = DataMap.newDataMap();
        }
        return userMap;
    }

    public abstract <T> T accept(ParseTreeVisitor<? extends T> visitor);


    protected abstract A asAntlrNode();


    protected interface AntlrToPmdParseTreeAdapter<N extends GenericNode<N>> extends ParseTree {

        BaseAntlrNode<?, N> getPmdNode();


        @Override
        AntlrToPmdParseTreeAdapter<N> getParent();
    }
}
