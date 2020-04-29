/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 */
public abstract class AbstractAntlrTerminalNode<N extends AntlrNode<N>>
    extends AbstractAntlrNode<TerminalNode, N> {

    protected AbstractAntlrTerminalNode(TerminalNode parseTree) {
        super(parseTree);
    }

    @Override
    public int getBeginLine() {
        return getParseTree().getSymbol().getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return getParseTree().getSymbol().getLine();
    }

    @Override
    public int getBeginColumn() {
        return AntlrUtils.getBeginColumn(getParseTree().getSymbol());
    }

    @Override
    public int getEndColumn() {
        return AntlrUtils.getEndColumn(getParseTree().getSymbol());
    }

}
