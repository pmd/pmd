/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrNode.AntlrToPmdParseTreeAdapter;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * Base class for an antlr node. This implements the PMD interfaces only,
 * not the antlr ones. It wraps an antlr node (they are linked both ways).
 * Antlr primarily distinguishes {@link ParserRuleContext} for inner nodes,
 * {@link TerminalNode} for nodes that wrap tokens (and can have no children),
 * and {@link ErrorNode}, a subtype of {@link TerminalNode}. These each have
 * a base class here, which refines the type of the underlying antlr node:
 * {@link BaseAntlrInnerNode}, {@link BaseAntlrTerminalNode} and {@link BaseAntlrErrorNode}.
 * These must be implemented in each language module with a class that also
 * implements {@code <N>}.
 *
 * <p>During tree construction, the antlr runtime does its thing with the
 * underlying antlr nodes. The PMD nodes are just wrappers around those,
 * that respect the contract of {@link GenericNode}.
 *
 * @param <A> Type of the underlying antlr node
 * @param <N> Public interface (eg SwiftNode)
 */
public abstract class BaseAntlrNode<A extends AntlrToPmdParseTreeAdapter<N>, N extends AntlrNode<N>> implements AntlrNode<N> {

    private DataMap<DataKey<?, ?>> userMap;

    /**
     * The only node for which this is not overwritten is the root node, for
     * which by contract, this is -1.
     */
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
    public FileLocation getReportLocation() {
        return getTextDocument().toLocation(TextRegion.fromBothOffsets(getFirstAntlrToken().getStartIndex(),
                                                                       getFirstAntlrToken().getStopIndex()));
    }

    void setIndexInParent(int indexInParent) {
        this.indexInParent = indexInParent;
    }

    @Override
    public N getParent() {
        AntlrToPmdParseTreeAdapter<N> parent = asAntlrNode().getParent();
        return parent == null ? null : (N) parent.getPmdNode();
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
        assert getParent() == null || indexInParent >= 0 : "Index not set";
        return indexInParent;
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        if (userMap == null) {
            userMap = DataMap.newDataMap();
        }
        return userMap;
    }

    protected abstract A asAntlrNode();


    protected interface AntlrToPmdParseTreeAdapter<N extends AntlrNode<N>> extends ParseTree {

        BaseAntlrNode<?, N> getPmdNode();


        @Override
        AntlrToPmdParseTreeAdapter<N> getParent();
    }
}
