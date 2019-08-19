/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.scala.ScalaParser;

import scala.meta.Tree;
import scala.meta.inputs.Position;

/**
 * The Java-wrapper for Scala Nodes to allow interoperability with PMD.
 */
public class ScalaWrapperNode extends AbstractNode implements ScalaNode {
    private Tree node;
    private ScalaParser parser;

    /**
     * Create a new Java Wrapper around a Scala node.
     * 
     * @param scalaParser
     *            the ScalaParser used to generate the node
     * @param scalaNode
     *            the scalaNode node to wrap
     */
    public ScalaWrapperNode(ScalaParser scalaParser, Tree scalaNode) {
        super(0);
        this.parser = scalaParser;
        this.node = scalaNode;
        Position pos = node.pos();
        beginLine = pos.startLine() + 1;
        endLine = pos.endLine() + 1;
        beginColumn = pos.startColumn() + 1;
        endColumn = pos.endColumn() + 1;
    }

    @Override
    public Tree getNode() {
        return node;
    }

    @Override
    public Object accept(ScalaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(ScalaParserVisitor visitor, Object data) {
        int numChildren = jjtGetNumChildren();
        for (int i = 0; i < numChildren; ++i) {
            jjtGetChild(i).accept(visitor, data);
        }
        return data;
    }

    @Override
    public String getXPathNodeName() {
        return node.productPrefix().replace(".", "");
    }

    @Override
    public void jjtClose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void jjtSetParent(Node parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScalaWrapperNode jjtGetParent() {
        if (node.parent().isEmpty()) {
            return null;
        }
        return parser.wrapNode(node.parent().get());
    }

    @Override
    public void jjtAddChild(Node child, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void jjtSetChildIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int jjtGetChildIndex() {
        int idx = node.parent().get().children().indexOf(node);
        if (idx == -1) {
            throw new IllegalStateException("This node is not a child of its parent: " + node);
        }
        return idx;
    }

    @Override
    public ScalaWrapperNode jjtGetChild(int index) {
        return parser.wrapNode(node.children().apply(index));
    }

    @Override
    public int jjtGetNumChildren() {
        return node.children().size();
    }

    @Override
    public int jjtGetId() {
        return 0;
    }

    @Override
    public String getImage() {
        String image = null;
        if (node instanceof scala.meta.Lit) {
            image = String.valueOf(((scala.meta.Lit) node).value());
        } else if (node instanceof scala.meta.Name) {
            image = ((scala.meta.Name) node).value().toString();
        } else if (node instanceof scala.meta.Type.Name) {
            image = ((scala.meta.Type.Name) node).value();
        } else if (node instanceof scala.meta.Term.Name) {
            image = ((scala.meta.Term.Name) node).value();
        } else if (node instanceof scala.meta.Type.Var.Name) {
            image = ((scala.meta.Type.Var.Name) node).value();
        }
        return image;
    }

    @Override
    public void setImage(String image) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasImageEqualTo(String image) {
        return Objects.equals(image, getImage());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChildAtIndex(int childIndex) {
        throw new UnsupportedOperationException();
    }

}
