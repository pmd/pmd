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
    }

    protected abstract Token getFirstToken();


    protected abstract Token getLastToken();

    @Override
    public N getParent() {
        return (N) asAntlrNode().getParent().getPmdNode();
    }

    @Override
    public int getBeginLine() {
        return getFirstToken().getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return getLastToken().getLine();
    }

    @Override
    public int getBeginColumn() {
        return getFirstToken().getCharPositionInLine() + 1;
    }

    @Override
    public int getEndColumn() {
        Token tok = getLastToken();
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
