/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 *
 */
public abstract class AbstractAntlrNode<
    B extends AbstractAntlrNode<B, ?, N>,
    T extends AntlrParseTreeBase,
    N extends GenericNode<N>
    > extends AbstractNode<B, N> {

    private final T parseTree;

    protected AbstractAntlrNode(T parseTree) {
        this.parseTree = parseTree;
    }


    @Override
    protected void addChild(B child, int index) {
        super.addChild(child, index);
    }

    protected T getParseTree() {
        return parseTree;
    }

    @Override
    public int getBeginLine() {
        return parseTree.start.getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return parseTree.stop.getLine();
    }

    @Override
    public int getBeginColumn() {
        return AntlrUtils.getBeginColumn(parseTree.start);
    }

    @Override
    public int getEndColumn() {
        return AntlrUtils.getEndColumn(parseTree.stop);
    }

}
