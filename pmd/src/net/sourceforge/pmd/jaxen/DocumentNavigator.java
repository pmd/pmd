/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.Node;
import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;
import org.saxpath.SAXPathException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author daniels
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class DocumentNavigator extends DefaultNavigator {

    /**
     * Constant: empty iterator.
     */
    private final static Iterator EMPTY_ITERATOR = new ArrayList().iterator();

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeName(java.lang.Object)
     */
    public String getAttributeName(Object arg0) {
        // TODO Auto-generated method stub
        return ((Attribute) arg0).getName();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeNamespaceUri(java.lang.Object)
     */
    public String getAttributeNamespaceUri(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeQName(java.lang.Object)
     */
    public String getAttributeQName(Object arg0) {
        // TODO Auto-generated method stub
        return ((Attribute) arg0).getName();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeStringValue(java.lang.Object)
     */
    public String getAttributeStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return ((Attribute) arg0).getValue();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getCommentStringValue(java.lang.Object)
     */
    public String getCommentStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementName(java.lang.Object)
     */
    public String getElementName(Object node) {
        return node.toString();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementNamespaceUri(java.lang.Object)
     */
    public String getElementNamespaceUri(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementQName(java.lang.Object)
     */
    public String getElementQName(Object arg0) {
        return getElementName(arg0);
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementStringValue(java.lang.Object)
     */
    public String getElementStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespacePrefix(java.lang.Object)
     */
    public String getNamespacePrefix(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespaceStringValue(java.lang.Object)
     */
    public String getNamespaceStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getTextStringValue(java.lang.Object)
     */
    public String getTextStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isAttribute(java.lang.Object)
     */
    public boolean isAttribute(Object arg0) {
        // TODO Auto-generated method stub
        return arg0 instanceof Attribute;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isComment(java.lang.Object)
     */
    public boolean isComment(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isDocument(java.lang.Object)
     */
    public boolean isDocument(Object arg0) {
        // TODO Auto-generated method stub
        return arg0 instanceof ASTCompilationUnit;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isElement(java.lang.Object)
     */
    public boolean isElement(Object arg0) {
        // TODO Auto-generated method stub
        return arg0 instanceof Node;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isNamespace(java.lang.Object)
     */
    public boolean isNamespace(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isProcessingInstruction(java.lang.Object)
     */
    public boolean isProcessingInstruction(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isText(java.lang.Object)
     */
    public boolean isText(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#parseXPath(java.lang.String)
     */
    public XPath parseXPath(String arg0) throws SAXPathException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getParentNode(java.lang.Object)
     */
    public Object getParentNode(Object arg0) {
        if (arg0 instanceof Node) {
            return ((Node) arg0).jjtGetParent();
        } else {
            return ((Attribute) arg0).getParent();
        }
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeAxisIterator(java.lang.Object)
     */
    public Iterator getAttributeAxisIterator(Object arg0) {
        Node contextNode = (Node) arg0;
        return new AttributeAxisIterator(contextNode);
    }

    /**
     * Get an iterator over all of this node's children.
     *
     * @param contextNode The context node for the child axis.
     * @return A possibly-empty iterator (not null).
     */
    public Iterator getChildAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            protected Node getFirstNode(Node node) {
                return getFirstChild(node);
            }

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
    public Iterator getParentAxisIterator(Object contextNode) {
        if (isAttribute(contextNode)) {
            return new SingleObjectIterator(((Attribute) contextNode).getParent());
        } else {
            Node parent = ((Node) contextNode).jjtGetParent();
            if (parent != null) {
                return new SingleObjectIterator(parent);
            } else {
                return EMPTY_ITERATOR;
            }
        }
    }

    /**
     * Get an iterator over all following siblings.
     *
     * @param contextNode the context node for the sibling iterator.
     * @return A possibly-empty iterator (not null).
     */
    public Iterator getFollowingSiblingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            protected Node getFirstNode(Node node) {
                return getNextNode(node);
            }

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
    public Iterator getPrecedingSiblingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            protected Node getFirstNode(Node node) {
                return getNextNode(node);
            }

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
    public Iterator getFollowingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            protected Node getFirstNode(Node node) {
                if (node == null)
                    return null;
                else {
                    Node sibling = getNextSibling(node);
                    if (sibling == null)
                        return getFirstNode(node.jjtGetParent());
                    else
                        return sibling;
                }
            }

            protected Node getNextNode(Node node) {
                if (node == null)
                    return null;
                else {
                    Node n = getFirstChild(node);
                    if (n == null)
                        n = getNextSibling(node);
                    if (n == null)
                        return getFirstNode(node.jjtGetParent());
                    else
                        return n;
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
    public Iterator getPrecedingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            protected Node getFirstNode(Node node) {
                if (node == null)
                    return null;
                else {
                    Node sibling = getPreviousSibling(node);
                    if (sibling == null)
                        return getFirstNode(node.jjtGetParent());
                    else
                        return sibling;
                }
            }

            protected Node getNextNode(Node node) {
                if (node == null)
                    return null;
                else {
                    Node n = getLastChild(node);
                    if (n == null)
                        n = getPreviousSibling(node);
                    if (n == null)
                        return getFirstNode(node.jjtGetParent());
                    else
                        return n;
                }
            }
        };
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getDocumentNode(java.lang.Object)
     */
    public Object getDocumentNode(Object contextNode) {
        if (isDocument(contextNode)) {
            return contextNode;
        } else {
            return getDocumentNode(getParentNode(contextNode));
        }
    }

}
