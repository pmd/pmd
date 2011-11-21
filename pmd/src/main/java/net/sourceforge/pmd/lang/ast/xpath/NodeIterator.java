/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author daniels
 */
public abstract class NodeIterator implements Iterator<Node> {

    private Node node;

    public NodeIterator(Node contextNode) {
        this.node = getFirstNode(contextNode);
    }

    public boolean hasNext() {
        return node != null;
    }

    public Node next() {
        if (node == null) {
            throw new NoSuchElementException();
        }
        Node ret = node;
        node = getNextNode(node);
        return ret;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract Node getFirstNode(Node contextNode);

    protected abstract Node getNextNode(Node contextNode);

    protected Node getPreviousSibling(Node contextNode) {
        Node parentNode = contextNode.jjtGetParent();
        if (parentNode != null) {
            int prevPosition = getPositionFromParent(contextNode) - 1;
            if (prevPosition >= 0) {
                return parentNode.jjtGetChild(prevPosition);
            }
        }
        return null;
    }

    private int getPositionFromParent(Node contextNode) {
        Node parentNode = contextNode.jjtGetParent();
        for (int i = 0; i < parentNode.jjtGetNumChildren(); i++) {
            if (parentNode.jjtGetChild(i) == contextNode) {
                return i;
            }
        }
        throw new RuntimeException("Node was not a child of it's parent ???");
    }

    protected Node getNextSibling(Node contextNode) {
        Node parentNode = contextNode.jjtGetParent();
        if (parentNode != null) {
            int nextPosition = getPositionFromParent(contextNode) + 1;
            if (nextPosition < parentNode.jjtGetNumChildren()) {
                return parentNode.jjtGetChild(nextPosition);
            }
        }
        return null;
    }

    protected Node getFirstChild(Node contextNode) {
        if (contextNode.jjtGetNumChildren() > 0) {
            return contextNode.jjtGetChild(0);
        } else {
            return null;
        }
    }

    protected Node getLastChild(Node contextNode) {
        if (contextNode.jjtGetNumChildren() > 0) {
            return contextNode.jjtGetChild(contextNode.jjtGetNumChildren() - 1);
        } else {
            return null;
        }
    }
}