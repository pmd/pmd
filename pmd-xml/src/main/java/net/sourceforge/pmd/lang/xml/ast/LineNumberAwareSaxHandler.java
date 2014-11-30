/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.ast;

import static net.sourceforge.pmd.lang.xml.ast.XmlNode.BEGIN_COLUMN;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.BEGIN_LINE;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.END_COLUMN;
import static net.sourceforge.pmd.lang.xml.ast.XmlNode.END_LINE;

import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.lang.xml.XmlParserOptions;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * SAX Handler to build a DOM Document with line numbers.
 * 
 * @see http://eyalsch.wordpress.com/2010/11/30/xml-dom-2/
 */
class LineNumberAwareSaxHandler extends DefaultHandler2 {
    private Stack<Node> nodeStack = new Stack<Node>();
    private StringBuilder text = new StringBuilder();
    private int beginLineText = -1;
    private int beginColumnText = -1;
    private Locator locator;
    private final DocumentBuilder documentBuilder;
    private final Document document;
    private boolean cdataEnded = false;

    private boolean coalescing;
    private boolean expandEntityReferences;
    private boolean ignoringComments;
    private boolean ignoringElementContentWhitespace;
    private boolean namespaceAware;

    public LineNumberAwareSaxHandler(XmlParserOptions options) throws ParserConfigurationException {
        // uses xerces directly, so that we can build a DTD / entities
        // section
        this.documentBuilder = new DocumentBuilderFactoryImpl().newDocumentBuilder();

        this.document = this.documentBuilder.newDocument();
        this.coalescing = options.isCoalescing();
        this.expandEntityReferences = options.isExpandEntityReferences();
        this.ignoringComments = options.isIgnoringComments();
        this.ignoringElementContentWhitespace = options.isIgnoringElementContentWhitespace();
        this.namespaceAware = options.isNamespaceAware();
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        addTextIfNeeded(false);

        Element element;
        if (namespaceAware) {
            element = document.createElementNS(uri, qName);
        } else {
            element = document.createElement(qName);
        }

        for (int i = 0; i < attributes.getLength(); i++) {
            String attQName = attributes.getQName(i);
            String attNamespaceURI = attributes.getURI(i);
            String attValue = attributes.getValue(i);
            Attr a;
            if (namespaceAware) {
                a = document.createAttributeNS(attNamespaceURI, attQName);
                element.setAttributeNodeNS(a);
            } else {
                a = document.createAttribute(attQName);
                element.setAttributeNode(a);
            }
            a.setValue(attValue);
        }

        element.setUserData(BEGIN_LINE, locator.getLineNumber(), null);
        element.setUserData(BEGIN_COLUMN, locator.getColumnNumber(), null);

        nodeStack.push(element);
    }

    private void addTextIfNeeded(boolean alwaysAdd) {
        if (text.length() > 0) {
            addTextNode(text.toString(), cdataEnded || alwaysAdd);
            text.setLength(0);
            cdataEnded = false;
        }
    }

    private void addTextNode(String s, boolean alwaysAdd) {
        if (alwaysAdd || !ignoringElementContentWhitespace || s.trim().length() > 0) {
            Text textNode = document.createTextNode(s);
            textNode.setUserData(BEGIN_LINE, beginLineText, null);
            textNode.setUserData(BEGIN_COLUMN, beginColumnText, null);
            textNode.setUserData(END_LINE, locator.getLineNumber(), null);
            textNode.setUserData(END_COLUMN, locator.getColumnNumber(), null);
            appendChild(textNode);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (text.length() == 0) {
            beginLineText = locator.getLineNumber();
            beginColumnText = locator.getColumnNumber();
        }
        text.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        addTextIfNeeded(false);
        Node element = nodeStack.pop();
        element.setUserData(END_LINE, locator.getLineNumber(), null);
        element.setUserData(END_COLUMN, locator.getColumnNumber(), null);
        appendChild(element);
    }

    @Override
    public void startDocument() throws SAXException {
        document.setUserData(BEGIN_LINE, locator.getLineNumber(), null);
        document.setUserData(BEGIN_COLUMN, locator.getColumnNumber(), null);
    }

    @Override
    public void endDocument() throws SAXException {
        addTextIfNeeded(false);
        document.setUserData(END_LINE, locator.getLineNumber(), null);
        document.setUserData(END_COLUMN, locator.getColumnNumber(), null);
    }

    @Override
    public void startCDATA() throws SAXException {
        if (!coalescing) {
            addTextIfNeeded(true);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (!coalescing) {
            CDATASection cdataSection = document.createCDATASection(text.toString());
            cdataSection.setUserData(BEGIN_LINE, beginLineText, null);
            cdataSection.setUserData(BEGIN_COLUMN, beginColumnText, null);
            cdataSection.setUserData(END_LINE, locator.getLineNumber(), null);
            cdataSection.setUserData(END_COLUMN, locator.getColumnNumber(), null);
            appendChild(cdataSection);
            text.setLength(0);
            cdataEnded = true;
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (!ignoringComments) {
            addTextIfNeeded(false);
            Comment comment = document.createComment(new String(ch, start, length));
            comment.setUserData(BEGIN_LINE, locator.getLineNumber(), null);
            comment.setUserData(BEGIN_COLUMN, locator.getColumnNumber(), null);
            comment.setUserData(END_LINE, locator.getLineNumber(), null);
            comment.setUserData(END_COLUMN, locator.getColumnNumber(), null);
            appendChild(comment);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        DocumentType docType = documentBuilder.getDOMImplementation().createDocumentType(name, publicId, systemId);
        docType.setUserData(BEGIN_LINE, locator.getLineNumber(), null);
        docType.setUserData(BEGIN_COLUMN, locator.getColumnNumber(), null);
        document.appendChild(docType);
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (!expandEntityReferences) {
            addTextIfNeeded(false);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (!expandEntityReferences) {
            EntityReference entity = document.createEntityReference(name);
            entity.setUserData(BEGIN_LINE, beginLineText, null);
            entity.setUserData(BEGIN_COLUMN, beginColumnText, null);
            entity.setUserData(END_LINE, locator.getLineNumber(), null);
            entity.setUserData(END_COLUMN, locator.getColumnNumber(), null);
            appendChild(entity);
            text.setLength(0); // throw the expanded entity text away
        }
    }

    @Override
    public void endDTD() throws SAXException {
        DocumentType doctype = document.getDoctype();
        doctype.setUserData(END_LINE, locator.getLineNumber(), null);
        doctype.setUserData(END_COLUMN, locator.getColumnNumber(), null);
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
        Entity entity = new ChangeableEntity(document, name);
        entity.appendChild(document.createTextNode(value));

        NamedNodeMap entities = document.getDoctype().getEntities();
        entities.setNamedItem(entity);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        ProcessingInstruction pi = document.createProcessingInstruction(target, data);
        appendChild(pi);
    }

    private void appendChild(Node node) {
        if (nodeStack.isEmpty()) {
            document.appendChild(node);
        } else {
            nodeStack.peek().appendChild(node);
        }
    }

    private static class ChangeableEntity extends EntityImpl {
        public ChangeableEntity(Document document, String name) {
            super((CoreDocumentImpl) document, name);
            flags = (short) (flags & ~READONLY); // make it changeable
                                                 // again, so that we can
                                                 // add a text node as child
        }
    }
}
