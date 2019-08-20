/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;

import scala.meta.Tree;
import scala.meta.inputs.Position;

/**
 * A Wrapper for translating the Scala Tree Nodes to PMD-compatible Java-base
 * Nodes.
 *
 * @param <T>
 *            the type of the Scala tree node
 */
public class AbstractScalaNode<T extends Tree> extends AbstractNode implements ScalaNode<T> {
    private final T node;

    /**
     * Create the node and configure line numbers.
     * 
     * @param treeNode
     *            the scala tree node this node wraps
     */
    public AbstractScalaNode(T treeNode) {
        super(0);
        node = treeNode;
        Position pos = node.pos();
        beginLine = pos.startLine() + 1;
        endLine = pos.endLine() + 1;
        beginColumn = pos.startColumn() + 1;
        endColumn = pos.endColumn() + 1;
    }

    @Override
    public Object accept(ScalaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(ScalaParserVisitor visitor, Object data) {
        int numChildren = jjtGetNumChildren();
        for (int i = 0; i < numChildren; ++i) {
            ((ScalaNode<?>) jjtGetChild(i)).accept(visitor, data);
        }
        return data;
    }

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public String getXPathNodeName() {
        return node.productPrefix().replace(".", "");
    }
}
