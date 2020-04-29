/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

/**
 *
 */
public abstract class AbstractAntlrInnerNode<T extends AntlrParseTreeBase, N extends AntlrNode<N>>
    extends AbstractAntlrNode<T, N> {

    protected AbstractAntlrInnerNode(T parseTree) {
        super(parseTree);
    }

    @Override
    public int getBeginLine() {
        return getParseTree().start.getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return getParseTree().stop.getLine();
    }

    @Override
    public int getBeginColumn() {
        return AntlrUtils.getBeginColumn(getParseTree().start);
    }

    @Override
    public int getEndColumn() {
        return AntlrUtils.getEndColumn(getParseTree().stop);
    }

}
