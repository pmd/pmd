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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.util.CompoundIterator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlParser {
    protected final XmlParserOptions parserOptions;
    protected Map<Node, XmlNode> nodeCache = new HashMap<Node, XmlNode>();
    
    public XmlParser(XmlParserOptions parserOptions) {
	this.parserOptions = parserOptions;
    }

    protected Document parseDocument(Reader reader) throws ParseException {
	nodeCache.clear();
	try {
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    documentBuilderFactory.setCoalescing(parserOptions.isCoalescing());
	    documentBuilderFactory.setExpandEntityReferences(parserOptions.isExpandEntityReferences());
	    documentBuilderFactory.setIgnoringComments(parserOptions.isIgnoringComments());
	    documentBuilderFactory.setIgnoringElementContentWhitespace(parserOptions.isIgnoringElementContentWhitespace());
	    documentBuilderFactory.setNamespaceAware(parserOptions.isNamespaceAware());
	    documentBuilderFactory.setValidating(parserOptions.isValidating());
	    documentBuilderFactory.setXIncludeAware(parserOptions.isXincludeAware());
	    

	    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    documentBuilder.setEntityResolver(parserOptions.getEntityResolver());
	    Document document = documentBuilder.parse(new InputSource(reader));
	    return document;
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
		    return Integer.valueOf(-1);
		} else if ("getBeginColumn".equals(method.getName())) {
		    return Integer.valueOf(-1);
		} else if ("getEndLine".equals(method.getName())) {
		    return Integer.valueOf(-1);
		} else if ("getEndColumn".equals(method.getName())) {
		    return Integer.valueOf(-1);
		} else if ("getNode".equals(method.getName())) {
		    return node;
		} else if ("getUserData".equals(method.getName())) {
		    return userData;
		} else if ("setUserData".equals(method.getName())) {
		    userData = args[0];
		    return null;
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
    }
}
