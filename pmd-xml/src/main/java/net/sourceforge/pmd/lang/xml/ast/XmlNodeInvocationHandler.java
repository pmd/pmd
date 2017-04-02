/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import static net.sourceforge.pmd.lang.xml.ast.XmlNode.BEGIN_COLUMN;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.BEGIN_LINE;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.END_COLUMN;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.END_LINE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.util.CompoundIterator;

public class XmlNodeInvocationHandler implements InvocationHandler {
    private final Node node;
    private Object userData;
    private XmlParser parser;

    public XmlNodeInvocationHandler(XmlParser parser, Node node) {
        this.parser = parser;
        this.node = node;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // XmlNode method?
        if (method.getDeclaringClass().isAssignableFrom(XmlNode.class)
                && !"java.lang.Object".equals(method.getDeclaringClass().getName())) {
            if ("jjtGetNumChildren".equals(method.getName())) {
                return node.hasChildNodes() ? node.getChildNodes().getLength() : 0;
            } else if ("jjtGetChild".equals(method.getName())) {
                return parser.createProxy(node.getChildNodes().item(((Integer) args[0]).intValue()));
            } else if ("jjtGetChildIndex".equals(method.getName())) {
                Node parent = node.getParentNode();
                NodeList childNodes = parent.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (node == childNodes.item(i)) {
                        return i;
                    }
                }
                throw new IllegalStateException("This node is not a child of its parent: " + node);
            } else if ("getImage".equals(method.getName())) {
                if (node instanceof Text) {
                    return ((Text) node).getData();
                } else {
                    return null;
                }
            } else if ("jjtGetParent".equals(method.getName())) {
                Node parent = node.getParentNode();
                if (parent != null && !(parent instanceof Document)) {
                    return parser.createProxy(parent);
                } else {
                    return null;
                }
            } else if ("getAttributeIterator".equals(method.getName())) {
                List<Iterator<Attribute>> iterators = new ArrayList<>();

                // Expose DOM Attributes
                final NamedNodeMap attributes = node.getAttributes();
                iterators.add(new Iterator<Attribute>() {
                    private int index;

                    public boolean hasNext() {
                        return attributes != null && index < attributes.getLength();
                    }

                    public Attribute next() {
                        Node attributeNode = attributes.item(index++);
                        return new Attribute(parser.createProxy(node), attributeNode.getNodeName(),
                                attributeNode.getNodeValue());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                });

                // Expose Text/CDATA nodes to have an 'Image' attribute like
                // AST Nodes
                if (proxy instanceof Text) {
                    iterators.add(Collections.singletonList(
                            new Attribute((net.sourceforge.pmd.lang.ast.Node) proxy, "Image", ((Text) proxy).getData()))
                            .iterator());
                }

                // Expose Java Attributes
                // iterators.add(new
                // AttributeAxisIterator((net.sourceforge.pmd.lang.ast.Node)
                // p));

                return new CompoundIterator<Attribute>(iterators.toArray(new Iterator[iterators.size()]));
            } else if ("getBeginLine".equals(method.getName())) {
                return getUserData(BEGIN_LINE);
            } else if ("getBeginColumn".equals(method.getName())) {
                return getUserData(BEGIN_COLUMN);
            } else if ("getEndLine".equals(method.getName())) {
                return getUserData(END_LINE);
            } else if ("getEndColumn".equals(method.getName())) {
                return getUserData(END_COLUMN);
            } else if ("getNode".equals(method.getName())) {
                return node;
            } else if ("getUserData".equals(method.getName())) {
                return userData;
            } else if ("setUserData".equals(method.getName())) {
                userData = args[0];
                return null;
            } else if ("isFindBoundary".equals(method.getName())) {
                return false;
            }
            throw new UnsupportedOperationException("Method not supported for XmlNode: " + method);
        } else {
            if ("toString".equals(method.getName())) {
                String s = node.getNodeName();
                s = s.replace("#", "");
                return s;
            }
            // Delegate method
            Object result = method.invoke(node, args);
            return result;
        }
    }

    private Integer getUserData(String key) {
        if (node.getUserData(key) != null) {
            return (Integer) node.getUserData(key);
        }
        return Integer.valueOf(-1);
    }
}
