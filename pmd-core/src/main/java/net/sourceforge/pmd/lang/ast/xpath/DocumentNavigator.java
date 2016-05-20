/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import java.util.ArrayList;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * @author daniels
 */
public class DocumentNavigator extends DefaultNavigator {

    private static final Iterator<Node> EMPTY_ITERATOR = new ArrayList<Node>().iterator();

    @Override
    public String getAttributeName(Object arg0) {
	return ((Attribute) arg0).getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object arg0) {
	return "";
    }

    @Override
    public String getAttributeQName(Object arg0) {
	return ((Attribute) arg0).getName();
    }

    @Override
    public String getAttributeStringValue(Object arg0) {
	return ((Attribute) arg0).getStringValue();
    }

    @Override
    public String getCommentStringValue(Object arg0) {
	return "";
    }

    @Override
    public String getElementName(Object node) {
	return node.toString();
    }

    @Override
    public String getElementNamespaceUri(Object arg0) {
	return "";
    }

    @Override
    public String getElementQName(Object arg0) {
	return getElementName(arg0);
    }

    @Override
    public String getElementStringValue(Object arg0) {
	return "";
    }

    @Override
    public String getNamespacePrefix(Object arg0) {
	return "";
    }

    @Override
    public String getNamespaceStringValue(Object arg0) {
	return "";
    }

    @Override
    public String getTextStringValue(Object arg0) {
	return "";
    }

    @Override
    public boolean isAttribute(Object arg0) {
	return arg0 instanceof Attribute;
    }

    @Override
    public boolean isComment(Object arg0) {
	return false;
    }

    @Override
    public boolean isDocument(Object arg0) {
	return arg0 instanceof RootNode;
    }

    @Override
    public boolean isElement(Object arg0) {
	return arg0 instanceof Node;
    }

    @Override
    public boolean isNamespace(Object arg0) {
	return false;
    }

    @Override
    public boolean isProcessingInstruction(Object arg0) {
	return false;
    }

    @Override
    public boolean isText(Object arg0) {
	return false;
    }

    @Override
    public XPath parseXPath(String arg0) {
	return null;
    }

    @Override
    public Object getParentNode(Object arg0) {
        if (arg0 instanceof Node) {
            return ((Node) arg0).jjtGetParent();
        }
        if (arg0 instanceof Attribute) {
            return ((Attribute) arg0).getParent();
        }
        // can't navigate to parent node...
        return null;
    }

    @Override
    public Iterator<Attribute> getAttributeAxisIterator(Object arg0) {
	if (arg0 instanceof AttributeNode) {
	    return ((AttributeNode) arg0).getAttributeIterator();
	} else {
	    return new AttributeAxisIterator((Node) arg0);
	}
    }

    /**
     * Get an iterator over all of this node's children.
     *
     * @param contextNode The context node for the child axis.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getChildAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getFirstChild(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getNextSibling(node);
	    }
	};
    }

    /**
     * Get a (single-member) iterator over this node's parent.
     *
     * @param contextNode the context node for the parent axis.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getParentAxisIterator(Object contextNode) {
	if (isAttribute(contextNode)) {
	    return new SingleObjectIterator(((Attribute) contextNode).getParent());
	}
	Node parent = ((Node) contextNode).jjtGetParent();
	if (parent != null) {
	    return new SingleObjectIterator(parent);
	} else {
	    return EMPTY_ITERATOR;
	}
    }

    /**
     * Get an iterator over all following siblings.
     *
     * @param contextNode the context node for the sibling iterator.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getFollowingSiblingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getNextNode(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getNextSibling(node);
	    }
	};
    }

    /**
     * Get an iterator over all preceding siblings.
     *
     * @param contextNode The context node for the preceding sibling axis.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getPrecedingSiblingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getNextNode(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getPreviousSibling(node);
	    }
	};
    }

    /**
     * Get an iterator over all following nodes, depth-first.
     *
     * @param contextNode The context node for the following axis.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getFollowingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node sibling = getNextSibling(node);
		    if (sibling == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return sibling;
		    }
		}
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node n = getFirstChild(node);
		    if (n == null) {
			n = getNextSibling(node);
		    }
		    if (n == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return n;
		    }
		}
	    }
	};
    }

    /**
     * Get an iterator over all preceding nodes, depth-first.
     *
     * @param contextNode The context node for the preceding axis.
     * @return A possibly-empty iterator (not null).
     */
    @Override
    public Iterator<Node> getPrecedingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node sibling = getPreviousSibling(node);
		    if (sibling == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return sibling;
		    }
		}
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node n = getLastChild(node);
		    if (n == null) {
			n = getPreviousSibling(node);
		    }
		    if (n == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return n;
		    }
		}
	    }
	};
    }

    @Override
    public Object getDocumentNode(Object contextNode) {
        if (isDocument(contextNode)) {
            return contextNode;
        }
        if (null == contextNode) {
            throw new RuntimeException("contextNode may not be null");
        }
        return getDocumentNode(getParentNode(contextNode));
    }
}
