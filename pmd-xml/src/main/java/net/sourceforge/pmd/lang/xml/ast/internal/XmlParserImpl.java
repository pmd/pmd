/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.util.document.TextDocument;


public class XmlParserImpl {

    private final XmlParserOptions parserOptions;
    private final Map<org.w3c.dom.Node, XmlNode> nodeCache = new HashMap<>();


    public XmlParserImpl(XmlParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }


    private Document parseDocument(String xmlData) throws ParseException {
        nodeCache.clear();
        try {

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
            return documentBuilder.parse(new InputSource(new StringReader(xmlData)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }


    public RootXmlNode parse(ParserTask task) {
        String xmlData = task.getSourceText();
        Document document = parseDocument(xmlData);
        RootXmlNode root = new RootXmlNode(this, document, task.getTextDocument());
        TextDocument textDocument = task.getTextDocument();
        DOMLineNumbers lineNumbers = new DOMLineNumbers(root, textDocument);
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

        private final TextDocument textDoc;

        RootXmlNode(XmlParserImpl parser, Node domNode, TextDocument textDoc) {
            super(parser, domNode);
            this.textDoc = textDoc;
        }

        @Override
        public @NonNull TextDocument getTextDocument() {
            return textDoc;
        }
    }

}
