/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;

/**
 * @internal None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
 */
@InternalApi // Marking this internal in case this class is changed to public
final class XmlParserImpl {
    // never throws on unresolved resource
    private static final EntityResolver SILENT_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0]));

    private final Map<org.w3c.dom.Node, XmlNode> nodeCache = new HashMap<>();


    private Document parseDocument(String xmlData) throws ParseException {
        nodeCache.clear();
        try {
            DocumentBuilderFactory dbf = XmlUtil.newDocumentBuilderFactory();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            dbf.setIgnoringComments(false);
            dbf.setIgnoringElementContentWhitespace(false);
            dbf.setCoalescing(false);
            // allow DOCTYPE but do not resolve external entities. See #SILENT_ENTITY_RESOLVER
            // XmlUtil already disabled external entities, so SYSTEM entities won't be read.
            // otherwise, we get an exception and can't parse XML files with a doctype declaration
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            dbf.setExpandEntityReferences(true);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            documentBuilder.setEntityResolver(SILENT_ENTITY_RESOLVER);
            return documentBuilder.parse(new InputSource(new StringReader(xmlData)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }


    public XmlRootNode parse(ParserTask task) {
        String xmlData = task.getSourceText();
        Document document = parseDocument(xmlData);
        XmlRootNode root = new XmlRootNode(this, document, task);
        DOMLineNumbers lineNumbers = new DOMLineNumbers(root, task.getTextDocument());
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
}
