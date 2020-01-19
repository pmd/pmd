/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

import scala.meta.Tree;
import scala.meta.inputs.Position;

/**
 * A Wrapper for translating the Scala Tree Nodes to PMD-compatible Java-base
 * Nodes.
 *
 * @param <T>
 *            the type of the Scala tree node
 */
abstract class AbstractScalaNode<T extends Tree> extends AbstractNode implements ScalaNode<T> {
    private final T node;
    private final Position pos;

    /**
     * Create the node and configure line numbers.
     *
     * @param treeNode
     *            the scala tree node this node wraps
     */
    AbstractScalaNode(T treeNode) {
        super(0);
        node = treeNode;
        pos = node.pos();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<? extends ScalaNode<?>> children() {
        return (Iterable<ScalaNode<?>>) super.children();
    }

    @Override
    public boolean isImplicit() {
        return pos.end() - pos.start() == 0;
    }

    @Override
    public int getBeginLine() {
        return pos.startLine() + 1;
    }

    @Override
    public int getBeginColumn() {
        return pos.startColumn() + 1;
    }

    @Override
    public int getEndLine() {
        return pos.endLine() + 1;
    }

    @Override
    public int getEndColumn() {
        return pos.endColumn(); // no +1
    }

    @Override
    public void testingOnlySetBeginColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testingOnlySetBeginLine(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testingOnlySetEndColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testingOnlySetEndLine(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data);

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public ScalaNode<?> getChild(int index) {
        return (ScalaNode<?>) super.getChild(index);
    }

    @Override
    public ScalaNode<?> getParent() {
        return (ScalaNode<?>) super.getParent();
    }

    @Override
    public String getXPathNodeName() {
        return node.productPrefix().replace(".", "");
    }

}
