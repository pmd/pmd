/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for node iterators used to implement XPath axis
 * iterators for Jaxen.
 *
 * @author daniels
 */
@Deprecated
@InternalApi
public abstract class NodeIterator implements Iterator<Node> {

    private Node node;

    protected NodeIterator(Node contextNode) {
        this.node = getFirstNode(contextNode);
    }

    @Override
    public boolean hasNext() {
        return node != null;
    }

    @Override
    public Node next() {
        if (node == null) {
            throw new NoSuchElementException();
        }
        Node ret = node;
        node = getNextNode(node);
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract Node getFirstNode(Node contextNode);

    protected abstract Node getNextNode(Node contextNode);

    protected Node getPreviousSibling(Node contextNode) {
        Node parentNode = contextNode.jjtGetParent();
        if (parentNode != null) {
            int prevPosition = contextNode.jjtGetChildIndex() - 1;
            if (prevPosition >= 0) {
                return parentNode.jjtGetChild(prevPosition);
            }
        }
        return null;
    }

    protected Node getNextSibling(Node contextNode) {
        Node parentNode = contextNode.jjtGetParent();
        if (parentNode != null) {
            int nextPosition = contextNode.jjtGetChildIndex() + 1;
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
