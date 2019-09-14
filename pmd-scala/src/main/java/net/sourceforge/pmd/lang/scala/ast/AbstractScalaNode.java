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

    /**
     * Create the node and configure line numbers.
     * 
     * @param treeNode
     *            the scala tree node this node wraps
     */
    AbstractScalaNode(T treeNode) {
        super(0);
        node = treeNode;
        Position pos = node.pos();
        beginLine = pos.startLine() + 1;
        endLine = pos.endLine() + 1;
        beginColumn = pos.startColumn() + 1;
        endColumn = pos.endColumn() + 1;
    }

    @Override
    public abstract <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data);

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public ScalaNode<?> jjtGetChild(int index) {
        return (ScalaNode<?>) super.jjtGetChild(index);
    }

    @Override
    public ScalaNode<?> jjtGetParent() {
        return (ScalaNode<?>) super.jjtGetParent();
    }

    @Override
    public String getXPathNodeName() {
        return node.productPrefix().replace(".", "");
    }

}
