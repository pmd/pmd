/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;


/**
 * Base class for all implementations of the Node interface.
 */
public abstract class AbstractNode implements Node {

    private static final Logger LOG = Logger.getLogger(AbstractNode.class.getName());


    protected Node parent;
    protected Node[] children;
    protected int childIndex;
    protected int id;

    private String image;
    protected int beginLine = -1;
    protected int endLine;
    protected int beginColumn = -1;
    protected int endColumn;
    private DataFlowNode dataFlowNode;
    private Object userData;
    protected GenericToken firstToken;
    protected GenericToken lastToken;

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

    @Override
    public void jjtOpen() {
        // to be overridden by subclasses
    }

    @Override
    public void jjtClose() {
        // to be overridden by subclasses
    }

    @Override
    public void jjtSetParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(Node child, int index) {
        if (children == null) {
            children = new Node[index + 1];
        } else if (index >= children.length) {
            Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.jjtSetChildIndex(index);
    }

    @Override
    public void jjtSetChildIndex(int index) {
        childIndex = index;
    }

    @Override
    public int jjtGetChildIndex() {
        return childIndex;
    }

    @Override
    public Node jjtGetChild(int index) {
        return children[index];
    }

    @Override
    public int jjtGetNumChildren() {
        return children == null ? 0 : children.length;
    }

    @Override
    public int jjtGetId() {
        return id;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean hasImageEqualTo(String image) {
        return Objects.equals(this.getImage(), image);
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    public void testingOnlySetBeginLine(int i) {
        this.beginLine = i;
    }

    @Override
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

    public void testingOnlySetBeginColumn(int i) {
        this.beginColumn = i;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    public void testingOnlySetEndLine(int i) {
        this.endLine = i;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    public void testingOnlySetEndColumn(int i) {
        this.endColumn = i;
    }

    @Override
    public DataFlowNode getDataFlowNode() {
        if (this.dataFlowNode == null) {
            if (this.parent != null) {
                return parent.getDataFlowNode();
            }
            return null; // TODO wise?
        }
        return dataFlowNode;
    }

    @Override
    public void setDataFlowNode(DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }


    @Override
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


    @Override
    public <T> T getFirstParentOfType(Class<T> parentType) {
        Node parentNode = jjtGetParent();
        while (parentNode != null && !parentType.isInstance(parentNode)) {
            parentNode = parentNode.jjtGetParent();
        }
        return parentType.cast(parentNode);
    }


    @Override
    public <T> List<T> getParentsOfType(Class<T> parentType) {
        List<T> parents = new ArrayList<>();
        Node parentNode = jjtGetParent();
        while (parentNode != null) {
            if (parentType.isInstance(parentNode)) {
                parents.add(parentType.cast(parentNode));
            }
            parentNode = parentNode.jjtGetParent();
        }
        return parents;
    }

    @SafeVarargs
    @Override
    public final <T> T getFirstParentOfAnyType(Class<? extends T>... parentTypes) {
        Node parentNode = jjtGetParent();
        while (parentNode != null) {
            for (Class<? extends T> c : parentTypes) {
                if (c.isInstance(parentNode)) {
                    return c.cast(parentNode);
                }
            }
            parentNode = parentNode.jjtGetParent();
        }
        return null;
    }

    @Override
    public <T> List<T> findDescendantsOfType(Class<T> targetType) {
        List<T> list = new ArrayList<>();
        findDescendantsOfType(this, targetType, list, false);
        return list;
    }

    // TODO : Add to Node interface in 7.0.0
    public <T> List<T> findDescendantsOfType(final Class<T> targetType, final boolean crossBoundaries) {
        final List<T> list = new ArrayList<>();
        findDescendantsOfType(this, targetType, list, crossBoundaries);
        return list;
    }

    @Override
    public <T> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossBoundaries) {
        findDescendantsOfType(this, targetType, results, crossBoundaries);
    }

    private static <T> void findDescendantsOfType(Node node, Class<T> targetType, List<T> results,
            boolean crossFindBoundaries) {

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (targetType.isAssignableFrom(child.getClass())) {
                results.add(targetType.cast(child));
            }

            if (crossFindBoundaries || !child.isFindBoundary()) {
                findDescendantsOfType(child, targetType, results, crossFindBoundaries);
            }
        }
    }


    @Override
    public <T> List<T> findChildrenOfType(Class<T> targetType) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            Node child = jjtGetChild(i);
            if (targetType.isInstance(child)) {
                list.add(targetType.cast(child));
            }
        }
        return list;
    }

    @Override
    public boolean isFindBoundary() {
        return false;
    }

    @Override
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
            // If the parentNode is a Document itself, it's ownerDocument is
            // null
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


    @Override
    public <T> T getFirstDescendantOfType(Class<T> descendantType) {
        return getFirstDescendantOfType(descendantType, this);
    }


    @Override
    public <T> T getFirstChildOfType(Class<T> childType) {
        int n = jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            Node child = jjtGetChild(i);
            if (childType.isInstance(child)) {
                return childType.cast(child);
            }
        }
        return null;
    }


    private static <T> T getFirstDescendantOfType(final Class<T> descendantType, final Node node) {
        final int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            Node n1 = node.jjtGetChild(i);
            if (descendantType.isAssignableFrom(n1.getClass())) {
                return descendantType.cast(n1);
            }
            if (!n1.isFindBoundary()) {
                final T n2 = getFirstDescendantOfType(descendantType, n1);
                if (n2 != null) {
                    return n2;
                }
            }
        }
        return null;
    }


    @Override
    public final <T> boolean hasDescendantOfType(Class<T> type) {
        return getFirstDescendantOfType(type) != null;
    }


    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     *
     * @deprecated Use {@link #hasDescendantOfAnyType(Class[])}
     */
    @Deprecated
    public final boolean hasDecendantOfAnyType(Class<?>... types) {
        return hasDescendantOfAnyType(types);
    }


    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     */
    public final boolean hasDescendantOfAnyType(Class<?>... types) {
        // TODO consider implementing that with a single traversal!
        // hasDescendantOfType could then be a special case of this one
        // But to really share implementations, getFirstDescendantOfType's
        // internal helper could have to give up some type safety to rely
        // instead on a getFirstDescendantOfAnyType, then cast to the correct type
        for (Class<?> type : types) {
            if (hasDescendantOfType(type)) {
                return true;
            }
        }
        return false;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Node> findChildNodesWithXPath(String xpathString) throws JaxenException {
        return new BaseXPath(xpathString, new DocumentNavigator()).selectNodes(this);
    }


    @Override
    public boolean hasDescendantMatchingXPath(String xpathString) {
        try {
            return !findChildNodesWithXPath(xpathString).isEmpty();
        } catch (JaxenException e) {
            throw new RuntimeException("XPath expression " + xpathString + " failed: " + e.getLocalizedMessage(), e);
        }
    }


    @Override
    public Object getUserData() {
        return userData;
    }


    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public GenericToken jjtGetFirstToken() {
        return firstToken;
    }

    public void jjtSetFirstToken(GenericToken token) {
        this.firstToken = token;
    }

    public GenericToken jjtGetLastToken() {
        return lastToken;
    }

    public void jjtSetLastToken(GenericToken token) {
        this.lastToken = token;
    }

    @Override
    public void remove() {
        // Detach current node of its parent, if any
        final Node parent = jjtGetParent();
        if (parent != null) {
            parent.removeChildAtIndex(jjtGetChildIndex());
            jjtSetParent(null);
        }

        // TODO [autofix]: Notify action for handling text edition
    }

    @Override
    public void removeChildAtIndex(final int childIndex) {
        if (0 <= childIndex && childIndex < jjtGetNumChildren()) {
            // Remove the child at the given index
            children = ArrayUtils.remove(children, childIndex);
            // Update the remaining & left-shifted children indexes
            for (int i = childIndex; i < jjtGetNumChildren(); i++) {
                jjtGetChild(i).jjtSetChildIndex(i);
            }
        }
    }


    /**
     * {@inheritDoc}
     *
     * <p>This default implementation adds compatibility with the previous
     * way to get the xpath node name, which used {@link Object#toString()}.
     *
     * <p>Please override it. It may be removed in a future major version.
     */
    @Override
    // @Deprecated // FUTURE 7.0.0 make abstract
    public String getXPathNodeName() {
        LOG.warning("getXPathNodeName should be overriden in classes derived from AbstractNode. "
                            + "The implementation is provided for compatibility with existing implementors,"
                            + "but could be declared abstract as soon as release " + PMDVersion.getNextMajorRelease()
                            + ".");
        return toString();
    }


    /**
     *
     *
     * @deprecated The equivalence between toString and a node's name could be broken as soon as release 7.0.0.
     *  Use getXPathNodeName for that purpose. The use for debugging purposes is not deprecated.
     */
    @Deprecated
    @Override
    public String toString() {
        return getXPathNodeName();
    }


    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return new AttributeAxisIterator(this);
    }
}
