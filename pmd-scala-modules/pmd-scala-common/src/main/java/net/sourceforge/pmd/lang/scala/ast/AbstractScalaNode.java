/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.util.document.FileLocation;

import scala.meta.Tree;
import scala.meta.inputs.Position;

/**
 * A Wrapper for translating the Scala Tree Nodes to PMD-compatible Java-base
 * Nodes.
 *
 * @param <T> the type of the Scala tree node
 */
abstract class AbstractScalaNode<T extends Tree> extends AbstractNode<AbstractScalaNode<?>, ScalaNode<?>> implements ScalaNode<T> {

    protected final T node;
    private final Position pos;

    /**
     * Create the node and configure line numbers.
     *
     * @param treeNode
     *            the scala tree node this node wraps
     */
    AbstractScalaNode(T treeNode) {
        super();
        node = treeNode;
        pos = node.pos();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof ScalaParserVisitor) {
            return this.acceptVisitor((ScalaParserVisitor<P, R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    protected abstract <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data);

    // overridden to make it visible
    @Override
    protected void addChild(AbstractScalaNode<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    public boolean isImplicit() {
        return pos.end() - pos.start() == 0;
    }

    @Override
    public FileLocation getReportLocation() {
        return FileLocation.location("TODO", pos.startLine() + 1, pos.startColumn() + 1, pos.endLine() + 1, pos.endColumn());
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
        return pos.endColumn() + 1;
    }

    @Override
    @Deprecated
    public T getNode() {
        return node;
    }

    @Override
    public String getXPathNodeName() {
        return node.productPrefix().replace(".", "");
    }

}
