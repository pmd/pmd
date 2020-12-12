/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;


public final class XmlParserImpl {

    private final Map<org.w3c.dom.Node, XmlNode> nodeCache = new HashMap<>();


    private Document parseDocument(String xmlData) throws ParseException {
        nodeCache.clear();
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            dbf.setIgnoringComments(false);
            dbf.setIgnoringElementContentWhitespace(false);
            dbf.setExpandEntityReferences(true);
            dbf.setCoalescing(false);
            dbf.setXIncludeAware(false);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            return documentBuilder.parse(new InputSource(new StringReader(xmlData)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }


    public RootXmlNode parse(Reader reader) {
        String xmlData;
        try {
            xmlData = IOUtils.toString(reader);
        } catch (IOException e) {
            throw new ParseException(e);
        }
        Document document = parseDocument(xmlData);
        RootXmlNode root = new RootXmlNode(this, document);
        DOMLineNumbers lineNumbers = new DOMLineNumbers(root, xmlData);
        lineNumbers.determine();
        nodeCache.put(document, root);
        return root;
    }


    /**
     * Gets the wrapper for a DOM node, implementing PMD interfaces.
     *
     * @param domNode The node to wrap
     *
     * @return The wrapper
     */
    XmlNode wrapDomNode(Node domNode) {
        XmlNode wrapper = nodeCache.get(domNode);
        if (wrapper == null) {
            wrapper = new XmlNodeWrapper(this, domNode);
            nodeCache.put(domNode, wrapper);
        }
        return wrapper;
    }


    /**
     * The root should implement {@link RootNode}.
     */
    public static class RootXmlNode extends XmlNodeWrapper implements RootNode {
        RootXmlNode(XmlParserImpl parser, Node domNode) {
            super(parser, domNode);
        }
    }

}
