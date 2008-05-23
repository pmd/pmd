package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;

    private String image;
    protected int beginLine = -1;
    protected int endLine;
    protected int beginColumn = -1;
    protected int endColumn;
    private DataFlowNode dataFlowNode;

    public AbstractNode(int id) {
	this.id = id;
    }

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    public void jjtSetParent(Node parent) {
	this.parent = parent;
    }

    public Node jjtGetParent() {
	return parent;
    }

    public void jjtAddChild(Node child, int index) {
	if (children == null) {
	    children = new Node[index + 1];
	} else if (index >= children.length) {
	    Node newChildren[] = new Node[index + 1];
	    System.arraycopy(children, 0, newChildren, 0, children.length);
	    children = newChildren;
	}
	children[index] = child;
    }

    public Node jjtGetChild(int index) {
	return children[index];
    }

    public int jjtGetNumChildren() {
	return children == null ? 0 : children.length;
    }

    public int jjtGetId() {
	return id;
    }

    /**
     * Subclasses should implement this method to return a name usable with
     * XPathRule for evaluating Element Names.
     */
    @Override
    public abstract String toString();

    public String getImage() {
	return image;
    }

    public void setImage(String image) {
	this.image = image;
    }

    public boolean hasImageEqualTo(String image) {
	return this.image != null && this.image.equals(image);
    }

    public int getBeginLine() {
	return beginLine;
    }

    public void testingOnly__setBeginLine(int i) {
	this.beginLine = i;
    }

    public int getBeginColumn() {
	if (beginColumn != -1) {
	    return beginColumn;
	} else {
	    if (children != null && children.length > 0) {
		return children[0].getBeginColumn();
	    } else {
		throw new RuntimeException("Unable to determine begining line of Node.");
	    }
	}
    }

    public void testingOnly__setBeginColumn(int i) {
	this.beginColumn = i;
    }

    public int getEndLine() {
	return endLine;
    }

    public void testingOnly__setEndLine(int i) {
	this.endLine = i;
    }

    public int getEndColumn() {
	return endColumn;
    }

    public void testingOnly__setEndColumn(int i) {
	this.endColumn = i;
    }

    public DataFlowNode getDataFlowNode() {
	if (this.dataFlowNode == null) {
	    if (this.parent != null) {
		return parent.getDataFlowNode();
	    }
	    return null; //TODO wise?
	}
	return dataFlowNode;
    }

    public void setDataFlowNode(DataFlowNode dataFlowNode) {
	this.dataFlowNode = dataFlowNode;
    }

    public Node getNthParent(int n) {
	Node result = null;
	for (int i = 0; i < n; i++) {
	    if (result == null) {
		result = this.jjtGetParent();
	    } else {
		result = result.jjtGetParent();
	    }
	}
	return result;
    }

    /**
     * Traverses up the tree to find the first parent instance of type parentType
     *
     * @param parentType class which you want to find.
     * @return Node of type parentType.  Returns null if none found.
     */
    public <T> T getFirstParentOfType(Class<T> parentType) {
	Node parentNode = jjtGetParent();
	while (parentNode != null && parentNode.getClass() != parentType) {
	    parentNode = parentNode.jjtGetParent();
	}
	return (T) parentNode;
    }

    /**
     * Traverses up the tree to find all of the parent instances of type parentType
     *
     * @param parentType classes which you want to find.
     * @return List of parentType instances found.
     */
    public <T> List<T> getParentsOfType(Class<T> parentType) {
	List<T> parents = new ArrayList<T>();
	Node parentNode = jjtGetParent();
	while (parentNode != null) {
	    if (parentNode.getClass() == parentType) {
		parents.add((T) parentNode);
	    }
	    parentNode = parentNode.jjtGetParent();
	}
	return parents;
    }

    public <T> List<T> findChildrenOfType(Class<T> targetType) {
	List<T> list = new ArrayList<T>();
	findChildrenOfType(targetType, list);
	return list;
    }

    public <T> void findChildrenOfType(Class<T> targetType, List<T> results) {
	findChildrenOfType(this, targetType, results, true);
    }

    public <T> void findChildrenOfType(Class<T> targetType, List<T> results, boolean crossBoundaries) {
	this.findChildrenOfType(this, targetType, results, crossBoundaries);
    }

    private <T> void findChildrenOfType(Node node, Class<T> targetType, List<T> results, boolean crossFindBoundaries) {
	if (node.getClass().equals(targetType)) {
	    results.add((T) node);
	}

	if (!crossFindBoundaries && node.isFindBoundary()) {
	    return;
	}

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    Node child = node.jjtGetChild(i);
	    if (child.jjtGetNumChildren() > 0) {
		findChildrenOfType(child, targetType, results, crossFindBoundaries);
	    } else {
		if (child.getClass().equals(targetType)) {
		    results.add((T) child);
		}
	    }
	}
    }

    public boolean isFindBoundary() {
	return false;
    }

    public Document getAsXml() {
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document document = db.newDocument();
	    appendElement(document);
	    return document;
	} catch (ParserConfigurationException pce) {
	    throw new RuntimeException(pce);
	}
    }

    protected void appendElement(org.w3c.dom.Node parentNode) {
	DocumentNavigator docNav = new DocumentNavigator();
	Document ownerDocument = parentNode.getOwnerDocument();
	if (ownerDocument == null) {
	    //If the parentNode is a Document itself, it's ownerDocument is null
	    ownerDocument = (Document) parentNode;
	}
	String elementName = docNav.getElementName(this);
	Element element = ownerDocument.createElement(elementName);
	parentNode.appendChild(element);
	for (Iterator<Attribute> iter = docNav.getAttributeAxisIterator(this); iter.hasNext();) {
	    Attribute attr = iter.next();
	    element.setAttribute(attr.getName(), attr.getValue());
	}
	for (Iterator<Node> iter = docNav.getChildAxisIterator(this); iter.hasNext();) {
	    AbstractNode child = (AbstractNode) iter.next();
	    child.appendElement(element);
	}
    }

    /**
     * Traverses down the tree to find the first child instance of type childType
     *
     * @param childType class which you want to find.
     * @return Node of type childType.  Returns <code>null</code> if none found.
     */
    public <T> T getFirstChildOfType(Class<T> childType) {
	return getFirstChildOfType(childType, this);
    }

    private <T> T getFirstChildOfType(Class<T> childType, Node node) {
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    Node n = node.jjtGetChild(i);
	    if (n != null) {
		if (n.getClass().equals(childType)) {
		    return (T) n;
		}
		T n2 = getFirstChildOfType(childType, n);
		if (n2 != null) {
		    return n2;
		}
	    }
	}
	return null;
    }

    /**
     * Finds if this node contains a child of the given type.
     * This is an utility method that uses {@link #findChildrenOfType(Class)}
     *
     * @param type the node type to search
     * @return <code>true</code> if there is at lease on child of the given type and <code>false</code> in any other case
     */
    public final <T> boolean containsChildOfType(Class<T> type) {
	return !findChildrenOfType(type).isEmpty();
    }

    public List findChildNodesWithXPath(String xpathString) throws JaxenException {
	return new BaseXPath(xpathString, new DocumentNavigator()).selectNodes(this);
    }
}
