/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import java.util.Comparator;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextRegion;

import scala.meta.Tree;
import scala.meta.inputs.Position;

/**
 * A Wrapper for translating the Scala Tree Nodes to PMD-compatible Java-base
 * Nodes.
 *
 * @param <T> the type of the Scala tree node
 */
abstract class AbstractScalaNode<T extends Tree> extends AbstractNode<AbstractScalaNode<?>, ScalaNode<?>> implements ScalaNode<T> {

    private static final Comparator<Position> POS_CMP =
        Comparator.comparingInt(Position::start).thenComparing(Position::end);

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
    public TextRegion getTextRegion() {
        return TextRegion.fromBothOffsets(pos.start(), pos.end());
    }

    @Override
    public int compareLocation(Node node) {
        if (node instanceof AbstractScalaNode) {
            return POS_CMP.compare(((AbstractScalaNode<?>) node).pos, pos);
        }
        return ScalaNode.super.compareLocation(node);
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
