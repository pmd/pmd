/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

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
    private Object userData;

    public AbstractNode(int id) {
    	this.id = id;
    }

    public AbstractNode(int id, int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn) {
    	this(id);
    	
    	beginLine = theBeginLine;
    	endLine = theEndLine;
    	beginColumn = theBeginColumn;
    	endColumn = theEndColumn;
    }
    
    public boolean isSingleLine() {
    	return beginLine == endLine;
    }
    
    public void jjtOpen() {
	// to be overridden by subclasses
    }

    public void jjtClose() {
	// to be overridden by subclasses
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
		    Node[] newChildren = new Node[index + 1];
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
		throw new RuntimeException("Unable to determine beginning line of Node.");
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

    /**
     * Returns the n-th parent or null if there are not <code>n</code> ancestors
     *
     * @param n how many ancestors to iterate over.
     * @return the n-th parent or null.
     * @throws IllegalArgumentException if <code>n</code> is not positive.
     */
    public Node getNthParent(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        Node result = this.jjtGetParent();
        for (int i = 1; i < n; i++) {
            if (result == null) {
                return null;
            }
            result = result.jjtGetParent();
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

    /**
     * {@inheritDoc}
     */
    public <T> List<T> findDescendantsOfType(Class<T> targetType) {
	List<T> list = new ArrayList<T>();
	findDescendantsOfType(this, targetType, list, true);
	return list;
    }

    /**
     * {@inheritDoc}
     */
    public <T> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossBoundaries) {
	findDescendantsOfType(this, targetType, results, crossBoundaries);
    }

    private static <T> void findDescendantsOfType(Node node, Class<T> targetType, List<T> results,
	    boolean crossFindBoundaries) {

	if (!crossFindBoundaries && node.isFindBoundary()) {
	    return;
	}

	int n = node.jjtGetNumChildren();
	for (int i = 0; i < n; i++) {
	    Node child = node.jjtGetChild(i);
	    if (child.getClass() == targetType) {
		results.add((T) child);
	    }

	    findDescendantsOfType(child, targetType, results, crossFindBoundaries);
	}
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> findChildrenOfType(Class<T> targetType) {
	List<T> list = new ArrayList<T>();
	int n = jjtGetNumChildren();
	for (int i = 0; i < n; i++) {
	    Node child = jjtGetChild(i);
	    if (child.getClass() == targetType) {
		list.add((T) child);
	    }
	}
	return list;
    }

    public boolean isFindBoundary() {
	return false;
    }

    public Document getAsDocument() {
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
	    element.setAttribute(attr.getName(), attr.getStringValue());
	}
	for (Iterator<Node> iter = docNav.getChildAxisIterator(this); iter.hasNext();) {
	    AbstractNode child = (AbstractNode) iter.next();
	    child.appendElement(element);
	}
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getFirstDescendantOfType(Class<T> descendantType) {
	return getFirstDescendantOfType(descendantType, this);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getFirstChildOfType(Class<T> childType) {
	int n = jjtGetNumChildren();
	for (int i = 0; i < n; i++) {
	    Node child = jjtGetChild(i);
	    if (child.getClass() == childType) {
		return (T) child;
	    }
	}
	return null;
    }

    private static <T> T getFirstDescendantOfType(Class<T> descendantType, Node node) {
	int n = node.jjtGetNumChildren();
	for (int i = 0; i < n; i++) {
	    Node n1 = node.jjtGetChild(i);
	    if (n1.getClass() == descendantType) {
		return (T) n1;
	    }
	    T n2 = getFirstDescendantOfType(descendantType, n1);
	    if (n2 != null) {
		return n2;
	    }
	}
	return null;
    }

    /**
     * {@inheritDoc}
     */
    public final <T> boolean hasDescendantOfType(Class<T> type) {
	return getFirstDescendantOfType(type) != null;
    }

    /**
     * 
     * @param types
     * @return boolean
     */
    public final boolean hasDecendantOfAnyType(Class<?>... types) {
    	for (Class<?> type : types) {
    		if (hasDescendantOfType(type)) return true;
    	}
    	return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public List findChildNodesWithXPath(String xpathString) throws JaxenException {
        return new BaseXPath(xpathString, new DocumentNavigator()).selectNodes(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDescendantMatchingXPath(String xpathString) {
        try {
            return !findChildNodesWithXPath(xpathString).isEmpty();
        } catch (JaxenException e) {
            throw new RuntimeException("XPath expression " + xpathString + " failed: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * {@inheritDoc}
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
