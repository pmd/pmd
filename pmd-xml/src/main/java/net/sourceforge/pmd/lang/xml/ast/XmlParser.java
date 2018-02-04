/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

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
import net.sourceforge.pmd.lang.xml.XmlParserOptions;


public class XmlParser {
    protected final XmlParserOptions parserOptions;
    protected Map<org.w3c.dom.Node, XmlNode> nodeCache = new HashMap<>();


    public XmlParser(XmlParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }


    protected Document parseDocument(Reader reader) throws ParseException {
        nodeCache.clear();
        try {
            String xmlData = IOUtils.toString(reader);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(parserOptions.isNamespaceAware());
            dbf.setValidating(parserOptions.isValidating());
            dbf.setIgnoringComments(parserOptions.isIgnoringComments());
            dbf.setIgnoringElementContentWhitespace(parserOptions.isIgnoringElementContentWhitespace());
            dbf.setExpandEntityReferences(parserOptions.isExpandEntityReferences());
            dbf.setCoalescing(parserOptions.isCoalescing());
            dbf.setXIncludeAware(parserOptions.isXincludeAware());
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            documentBuilder.setEntityResolver(parserOptions.getEntityResolver());
            Document document = documentBuilder.parse(new InputSource(new StringReader(xmlData)));
            DOMLineNumbers lineNumbers = new DOMLineNumbers(document, xmlData);
            lineNumbers.determine();
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }


    public XmlNode parse(Reader reader) {
        Document document = parseDocument(reader);
        XmlNode root = new RootXmlNode(this, document);
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
        RootXmlNode(XmlParser parser, Node domNode) {
            super(parser, domNode);
        }
    }

}
