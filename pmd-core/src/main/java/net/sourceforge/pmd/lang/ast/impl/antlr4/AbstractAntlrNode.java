/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.impl.AbstractNode;

/**
 *
 */
public abstract class AbstractAntlrNode<T, N extends AntlrNode<N>>
    extends AbstractNode<AbstractAntlrNode<?, N>, N> implements AntlrNode<N> {

    private final T parseTree;

    protected AbstractAntlrNode(T parseTree) {
        this.parseTree = parseTree;
    }

    @Override
    protected void addChild(AbstractAntlrNode<?, N> child, int index) {
        super.addChild(child, index);
    }

    public T getParseTree() {
        return parseTree;
    }

    @Override
    public String toString() {
        return "AbstractAntlrNode{" +
            "parseTree=" + parseTree +
            '}';
    }


}
