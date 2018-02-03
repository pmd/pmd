/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jaxen.JaxenException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.CompoundIterator;


/**
 * Replaces the proxy.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class XmlNodeWrapper implements XmlNode, org.w3c.dom.Node {

    private final XmlParser parser;
    private final org.w3c.dom.Node node;
    private Object userData;


    public XmlNodeWrapper(XmlParser parser, org.w3c.dom.Node domNode) {
        this.parser = parser;
        this.node = domNode;
    }


    @Override
    public void jjtOpen() {

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
    public int jjtGetNumChildren() {
        return node.hasChildNodes() ? node.getChildNodes().getLength() : 0;
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
    public Node getNthParent(int n) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> T getFirstParentOfType(Class<T> parentType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> List<T> getParentsOfType(Class<T> parentType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> List<T> findChildrenOfType(Class<T> childType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> List<T> findDescendantsOfType(Class<T> targetType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossFindBoundaries) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> T getFirstChildOfType(Class<T> childType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> T getFirstDescendantOfType(Class<T> descendantType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> boolean hasDescendantOfType(Class<T> type) {
        throw new UnsupportedOperationException();
    }


    @Override
    public List<? extends Node> findChildNodesWithXPath(String xpathString) throws JaxenException {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean hasDescendantMatchingXPath(String xpathString) {
        throw new UnsupportedOperationException();
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
    public Iterator<Attribute> getAttributeIterator() {
        List<Iterator<Attribute>> iterators = new ArrayList<>();

        // Expose DOM Attributes
        final NamedNodeMap attributes = node.getAttributes();
        iterators.add(new Iterator<Attribute>() {
            private int index;


            public boolean hasNext() {
                return attributes != null && index < attributes.getLength();
            }


            public Attribute next() {
                org.w3c.dom.Node attributeNode = attributes.item(index++);
                return new Attribute(parser.wrapDomNode(node), attributeNode.getNodeName(),
                                     attributeNode.getNodeValue());
            }


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

        return new CompoundIterator<Attribute>(iterators.toArray(new Iterator[iterators.size()]));
    }


    @Override
    public org.w3c.dom.Node getNode() {
        return node;
    }

    /*
     * DELEGATED METHODS
     */


    @Override
    public String getNodeName() {
        return node.getNodeName();
    }


    @Override
    public String getNodeValue() throws DOMException {
        return node.getNodeValue();
    }


    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        node.setNodeValue(nodeValue);
    }


    @Override
    public short getNodeType() {
        return node.getNodeType();
    }


    @Override
    public org.w3c.dom.Node getParentNode() {
        return node.getParentNode();
    }


    @Override
    public NodeList getChildNodes() {
        return node.getChildNodes();
    }


    @Override
    public org.w3c.dom.Node getFirstChild() {
        return node.getFirstChild();
    }


    @Override
    public org.w3c.dom.Node getLastChild() {
        return node.getLastChild();
    }


    @Override
    public org.w3c.dom.Node getPreviousSibling() {
        return node.getPreviousSibling();
    }


    @Override
    public org.w3c.dom.Node getNextSibling() {
        return node.getNextSibling();
    }


    @Override
    public NamedNodeMap getAttributes() {
        return node.getAttributes();
    }


    @Override
    public Document getOwnerDocument() {
        return node.getOwnerDocument();
    }


    @Override
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        return node.insertBefore(newChild, refChild);
    }


    @Override
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        return node.replaceChild(newChild, oldChild);
    }


    @Override
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        return node.removeChild(oldChild);
    }


    @Override
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        return node.appendChild(newChild);
    }


    @Override
    public boolean hasChildNodes() {
        return node.hasChildNodes();
    }


    @Override
    public org.w3c.dom.Node cloneNode(boolean deep) {
        return node.cloneNode(deep);
    }


    @Override
    public void normalize() {
        node.normalize();
    }


    @Override
    public boolean isSupported(String feature, String version) {
        return node.isSupported(feature, version);
    }


    @Override
    public String getNamespaceURI() {
        return node.getNamespaceURI();
    }


    @Override
    public String getPrefix() {
        return node.getPrefix();
    }


    @Override
    public void setPrefix(String prefix) throws DOMException {
        node.setPrefix(prefix);
    }


    @Override
    public String getLocalName() {
        return node.getLocalName();
    }


    @Override
    public boolean hasAttributes() {
        return node.hasAttributes();
    }


    @Override
    public String getBaseURI() {
        return node.getBaseURI();
    }


    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        return node.compareDocumentPosition(other);
    }


    @Override
    public String getTextContent() throws DOMException {
        return node.getTextContent();
    }


    @Override
    public void setTextContent(String textContent) throws DOMException {
        node.setTextContent(textContent);
    }


    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return node.isSameNode(other);
    }


    @Override
    public String lookupPrefix(String namespaceURI) {
        return node.lookupPrefix(namespaceURI);
    }


    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return node.isDefaultNamespace(namespaceURI);
    }


    @Override
    public String lookupNamespaceURI(String prefix) {
        return node.lookupNamespaceURI(prefix);
    }


    @Override
    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return node.isEqualNode(arg);
    }


    @Override
    public Object getFeature(String feature, String version) {
        return node.getFeature(feature, version);
    }


    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return node.setUserData(key, data, handler);
    }


    @Override
    public Object getUserData(String key) {
        return node.getUserData(key);
    }

}
