/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.util.CompoundIterator;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;


/**
 * Proxy wrapping an XML DOM node ({@link org.w3c.dom.Node}) to implement PMD interfaces.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
class XmlNodeWrapper implements XmlNode {

    int beginLine = -1;
    int endLine = -1;
    int beginColumn = -1;
    int endColumn = -1;

    private DataMap<DataKey<?, ?>> dataMap;
    private final XmlParserImpl parser;
    private final org.w3c.dom.Node node;


    XmlNodeWrapper(XmlParserImpl parser, org.w3c.dom.Node domNode) {
        super();
        this.node = domNode;
        this.parser = parser;
    }

    @Override
    public XmlNode getParent() {
        org.w3c.dom.Node parent = node.getParentNode();
        return parent != null ? parser.wrapDomNode(parent) : null;
    }


    @Override
    public int getIndexInParent() {
        org.w3c.dom.Node parent = node.getParentNode();
        if (parent == null) {
            return -1;
        }
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (node == childNodes.item(i)) {
                return i;
            }
        }
        throw new IllegalStateException("This node is not a child of its parent: " + node);
    }


    @Override
    public XmlNode getChild(int index) {
        return parser.wrapDomNode(node.getChildNodes().item(index));
    }


    @Override
    public int getNumChildren() {
        return node.hasChildNodes() ? node.getChildNodes().getLength() : 0;
    }

    @Override
    public String getImage() {
        return node instanceof Text ? ((Text) node).getData() : null;
    }

    @Override
    public boolean hasImageEqualTo(String image) {
        return Objects.equals(image, getImage());
    }

    @Override
    public boolean isFindBoundary() {
        return false;
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        if (dataMap == null) {
            dataMap = DataMap.newDataMap();
        }
        return dataMap;
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

    @Override
    public org.w3c.dom.Node getNode() {
        return node;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    // package private, open only to DOMLineNumbers

    void setBeginLine(int i) {
        this.beginLine = i;
    }

    void setBeginColumn(int i) {
        this.beginColumn = i;
    }

    void setEndLine(int i) {
        this.endLine = i;
    }

    void setEndColumn(int i) {
        this.endColumn = i;
    }

}
