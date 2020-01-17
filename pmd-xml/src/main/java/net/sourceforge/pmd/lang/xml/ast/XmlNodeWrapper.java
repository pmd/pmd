/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.CompoundIterator;


/**
 * Proxy wrapping an XML DOM node ({@link org.w3c.dom.Node}) to implement PMD interfaces.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class XmlNodeWrapper extends AbstractDomNodeProxy implements XmlNode {

    private final XmlParser parser;
    private Object userData;


    public XmlNodeWrapper(XmlParser parser, org.w3c.dom.Node domNode) {
        super(domNode);
        this.parser = parser;
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
    public XmlNode jjtGetParent() {
        org.w3c.dom.Node parent = node.getParentNode();
        return parent != null ? parser.wrapDomNode(parent) : null;
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
        org.w3c.dom.Node parent = node.getParentNode();
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (node == childNodes.item(i)) {
                return i;
            }
        }
        throw new IllegalStateException("This node is not a child of its parent: " + node);
    }


    @Override
    public XmlNode jjtGetChild(int index) {
        return parser.wrapDomNode(node.getChildNodes().item(index));
    }

    @Override
    public Node getChild(int index) {
        return jjtGetChild(index);
    }

    @Override
    public int jjtGetNumChildren() {
        return node.hasChildNodes() ? node.getChildNodes().getLength() : 0;
    }


    @Override
    public int getNumChildren() {
        return jjtGetNumChildren();
    }

    @Override
    public int jjtGetId() {
        return 0;
    }


    @Override
    public String getImage() {
        return node instanceof Text ? ((Text) node).getData() : null;
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
    public int getBeginLine() {
        return (int) getUserData(BEGIN_LINE);
    }


    @Override
    public int getBeginColumn() {
        return (int) getUserData(BEGIN_COLUMN);
    }


    @Override
    public int getEndLine() {
        return (int) getUserData(END_LINE);
    }


    @Override
    public int getEndColumn() {
        return (int) getUserData(END_COLUMN);
    }


    @Override
    public DataFlowNode getDataFlowNode() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setDataFlowNode(DataFlowNode dataFlowNode) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isFindBoundary() {
        return false;
    }

    @Override
    public Document getAsDocument() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Object getUserData() {
        return userData;
    }


    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }


    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void removeChildAtIndex(int childIndex) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getXPathNodeName() {
        return node.getNodeName().replace("#", "");
    }


    @Override
    public String toString() {
        return node.getNodeName().replace("#", "");
    }


    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        List<Iterator<Attribute>> iterators = new ArrayList<>();

        // Expose DOM Attributes
        final NamedNodeMap attributes = node.getAttributes();
        iterators.add(new Iterator<Attribute>() {
            private int index;


            @Override
            public boolean hasNext() {
                return attributes != null && index < attributes.getLength();
            }


            @Override
            public Attribute next() {
                org.w3c.dom.Node attributeNode = attributes.item(index++);
                return new Attribute(XmlNodeWrapper.this,
                                     attributeNode.getNodeName(),
                                     attributeNode.getNodeValue());
            }


            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });

        // Expose Text/CDATA nodes to have an 'Image' attribute like AST Nodes
        if (node instanceof Text) {
            iterators.add(Collections.singletonList(new Attribute(this, "Image", ((Text) node).getData())).iterator());
        }

        // Expose Java Attributes
        // iterators.add(new AttributeAxisIterator((net.sourceforge.pmd.lang.ast.Node) p));

        @SuppressWarnings("unchecked")
        Iterator<Attribute>[] it = new Iterator[iterators.size()];

        return new CompoundIterator<>(iterators.toArray(it));
    }


    /**
     * @deprecated use {@link #getXPathAttributesIterator()}
     */
    @Override
    @Deprecated
    public Iterator<Attribute> getAttributeIterator() {
        return getXPathAttributesIterator();
    }


    @Override
    public org.w3c.dom.Node getNode() {
        return node;
    }
}
