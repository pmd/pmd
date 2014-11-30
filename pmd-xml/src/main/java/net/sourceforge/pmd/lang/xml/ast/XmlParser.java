/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.ast;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XmlParser {
    protected final XmlParserOptions parserOptions;
    protected Map<Node, XmlNode> nodeCache = new HashMap<Node, XmlNode>();

    public XmlParser(XmlParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }

    protected Document parseDocument(Reader reader) throws ParseException {
        nodeCache.clear();
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxParserFactory.setNamespaceAware(parserOptions.isNamespaceAware());
            saxParserFactory.setValidating(parserOptions.isValidating());
            saxParserFactory.setXIncludeAware(parserOptions.isXincludeAware());
            SAXParser saxParser = saxParserFactory.newSAXParser();

            LineNumberAwareSaxHandler handler = new LineNumberAwareSaxHandler(parserOptions);
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler", handler);
            xmlReader.setEntityResolver(parserOptions.getEntityResolver());

            xmlReader.parse(new InputSource(reader));
            return handler.getDocument();
        } catch (ParserConfigurationException e) {
            throw new ParseException(e);
        } catch (SAXException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public XmlNode parse(Reader reader) {
        Document document = parseDocument(reader);
        return createProxy(document);
    }

    public XmlNode createProxy(Node node) {
        XmlNode proxy = nodeCache.get(node);
        if (proxy != null) {
            return proxy;
        }

        // TODO Change Parser interface to take ClassLoader?
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.add(XmlNode.class);
        if (node instanceof Document) {
            interfaces.add(RootNode.class);
        }
        addAllInterfaces(interfaces, node.getClass());

        proxy = (XmlNode) Proxy.newProxyInstance(XmlParser.class.getClassLoader(),
                interfaces.toArray(new Class[interfaces.size()]), new XmlNodeInvocationHandler(this, node));
        nodeCache.put(node, proxy);
        return proxy;
    }

    public void addAllInterfaces(Set<Class<?>> interfaces, Class<?> clazz) {
        interfaces.addAll(Arrays.asList((Class<?>[]) clazz.getInterfaces()));
        if (clazz.getSuperclass() != null) {
            addAllInterfaces(interfaces, clazz.getSuperclass());
        }
    }

}
