/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.ast;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.util.CompoundIterator;

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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

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

    /**
     * SAX Handler to build a DOM Document with line numbers.
     * @see http://eyalsch.wordpress.com/2010/11/30/xml-dom-2/
     */
    private static class LineNumberAwareSaxHandler extends DefaultHandler2 {
        public static final String BEGIN_LINE = "pmd:beginLine";
        public static final String BEGIN_COLUMN = "pmd:beginColumn";
        public static final String END_LINE = "pmd:endLine";
        public static final String END_COLUMN = "pmd:endColumn";

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
            // uses xerces directly, so that we can build a DTD / entities section
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
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
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
            DocumentType docType = documentBuilder
                    .getDOMImplementation()
                    .createDocumentType(name, publicId, systemId);
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
                super((CoreDocumentImpl)document, name);
                flags = (short) (flags & ~READONLY); // make it changeable again, so that we can add a text node as child
            }
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

	proxy = (XmlNode) Proxy.newProxyInstance(XmlParser.class.getClassLoader(), interfaces
		.toArray(new Class[interfaces.size()]), new XmlNodeInvocationHandler(node));
	nodeCache.put(node, proxy);
	return proxy;
    }

    public void addAllInterfaces(Set<Class<?>> interfaces, Class<?> clazz) {
	interfaces.addAll(Arrays.asList((Class<?>[]) clazz.getInterfaces()));
	if (clazz.getSuperclass() != null) {
	    addAllInterfaces(interfaces, clazz.getSuperclass());
	}
    }

    public class XmlNodeInvocationHandler implements InvocationHandler {
	private final Node node;
	private Object userData;

	public XmlNodeInvocationHandler(Node node) {
	    this.node = node;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    // XmlNode method?
	    if (method.getDeclaringClass().isAssignableFrom(XmlNode.class)
		    && !"java.lang.Object".equals(method.getDeclaringClass().getName())) {
		if ("jjtGetNumChildren".equals(method.getName())) {
		    return node.hasChildNodes() ? node.getChildNodes().getLength() : 0;
		} else if ("jjtGetChild".equals(method.getName())) {
		    return createProxy(node.getChildNodes().item(((Integer) args[0]).intValue()));
		} else if ("getImage".equals(method.getName())) {
		    if (node instanceof Text) {
			return ((Text) node).getData();
		    } else {
			return null;
		    }
		} else if ("jjtGetParent".equals(method.getName())) {
		    Node parent = node.getParentNode();
		    if (parent != null && !(parent instanceof Document)) {
			return createProxy(parent);
		    } else {
			return null;
		    }
		} else if ("getAttributeIterator".equals(method.getName())) {
		    List<Iterator<Attribute>> iterators = new ArrayList<Iterator<Attribute>>();

		    // Expose DOM Attributes
		    final NamedNodeMap attributes = node.getAttributes();
		    iterators.add(new Iterator<Attribute>() {
			private int index;

			public boolean hasNext() {
			    return attributes != null && index < attributes.getLength();
			}

			public Attribute next() {
			    Node attributeNode = attributes.item(index++);
			    return new Attribute(createProxy(node), attributeNode.getNodeName(), attributeNode
				    .getNodeValue());
			}

			public void remove() {
			    throw new UnsupportedOperationException();
			}
		    });

		    // Expose Text/CDATA nodes to have an 'Image' attribute like AST Nodes
		    if (proxy instanceof Text) {
			iterators.add(Collections.singletonList(
				new Attribute((net.sourceforge.pmd.lang.ast.Node) proxy, "Image", ((Text) proxy)
					.getData())).iterator());
		    }

		    // Expose Java Attributes
		    // iterators.add(new AttributeAxisIterator((net.sourceforge.pmd.lang.ast.Node) p));

		    return new CompoundIterator<Attribute>(iterators.toArray(new Iterator[iterators.size()]));
		} else if ("getBeginLine".equals(method.getName())) {
		    return getUserData(LineNumberAwareSaxHandler.BEGIN_LINE);
		} else if ("getBeginColumn".equals(method.getName())) {
            return getUserData(LineNumberAwareSaxHandler.BEGIN_COLUMN);
		} else if ("getEndLine".equals(method.getName())) {
		    return getUserData(LineNumberAwareSaxHandler.END_LINE);
		} else if ("getEndColumn".equals(method.getName())) {
		    return getUserData(LineNumberAwareSaxHandler.END_COLUMN);
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
	    }
	    // Delegate method
	    else {
		if ("toString".equals(method.getName())) {
		    String s = node.getNodeName();
		    s = s.replace("#", "");
		    return s;
		}
		Object result = method.invoke(node, args);
		return result;
	    }
	}

    private Integer getUserData(String key) {
        if (node.getUserData(key) != null) {
            return (Integer)node.getUserData(key);
        }
        return Integer.valueOf(-1);
    }
    }
}
