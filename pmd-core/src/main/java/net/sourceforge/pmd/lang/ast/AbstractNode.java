/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Collections;
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
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;


/**
 * Base class for all implementations of the Node interface.
 *
 * <p>Please use the {@link Node} interface wherever possible and
 * not this class, unless you're compelled to do so.
 *
 * <p>Note that nearly all methods of the {@link Node} interface
 * will have default implementations with PMD 7.0.0, so that it
 * will not be necessary to extend this class directly.
 */
public abstract class AbstractNode implements Node {

    private static final Logger LOG = Logger.getLogger(AbstractNode.class.getName());

    /**
     * @deprecated Use {@link #getParent()}
     */
    @Deprecated
    protected Node parent;
    @Deprecated
    protected Node[] children;
    /** @deprecated Use {@link #getIndexInParent()} */
    @Deprecated
    protected int childIndex;
    /** @deprecated Use {@link #jjtGetId()} if you are a jjtree node. */
    @Deprecated
    protected int id;
    /** @deprecated This will be removed to delegate to the tokens for nodes that are backed by tokens. */
    @Deprecated
    protected int beginLine = -1;
    /** @deprecated This will be removed to delegate to the tokens for nodes that are backed by tokens. */
    @Deprecated
    protected int endLine;
    /** @deprecated This will be removed to delegate to the tokens for nodes that are backed by tokens. */
    @Deprecated
    protected int beginColumn = -1;
    /** @deprecated This will be removed to delegate to the tokens for nodes that are backed by tokens. */
    @Deprecated
    protected int endColumn;
    // Those should have been private.
    @Deprecated
    protected GenericToken firstToken;
    @Deprecated
    protected GenericToken lastToken;
    private DataFlowNode dataFlowNode;
    private Object userData;
    // @Deprecated?
    private String image;

    public AbstractNode(final int id) {
        this.id = id;
    }

    public AbstractNode(final int id, final int theBeginLine, final int theEndLine, final int theBeginColumn,
                        final int theEndColumn) {
        this(id);

        beginLine = theBeginLine;
        endLine = theEndLine;
        beginColumn = theBeginColumn;
        endColumn = theEndColumn;
    }


    @Override
    public Node getParent() {
        return jjtGetParent();
    }

    @Override
    public int getIndexInParent() {
        return jjtGetChildIndex();
    }

    @Override
    public Node getChild(final int index) {
        if (children == null) {
            throw new IndexOutOfBoundsException();
        }
        return children[index];
    }

    @Override
    public int getNumChildren() {
        return jjtGetNumChildren();
    }


    /**
     * @deprecated This is never used and is trivial, will be removed from this class.
     */
    @Deprecated
    public boolean isSingleLine() {
        return beginLine == endLine;
    }

    @Override
    @Deprecated
    @InternalApi
    public void jjtOpen() {
        // to be overridden by subclasses
    }

    @Override
    @Deprecated
    @InternalApi
    public void jjtClose() {
        // to be overridden by subclasses
    }

    @Override
    @Deprecated
    @InternalApi
    public void jjtSetParent(final Node parent) {
        this.parent = parent;
    }

    @Override
    @Deprecated
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    @Deprecated
    @InternalApi
    public void jjtAddChild(final Node child, final int index) {
        if (children == null) {
            children = new Node[index + 1];
        } else if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.jjtSetChildIndex(index);
    }

    @Override
    @Deprecated
    @InternalApi
    public void jjtSetChildIndex(final int index) {
        childIndex = index;
    }

    @Override
    @Deprecated
    public int jjtGetChildIndex() {
        return childIndex;
    }


    @Override
    @Deprecated
    public Node jjtGetChild(final int index) {
        return children[index];
    }

    @Override
    @Deprecated
    public int jjtGetNumChildren() {
        return children == null ? 0 : children.length;
    }


    /**
     * @deprecated Will be made protected with 7.0.0.
     */
    @Override
    @Deprecated
    public int jjtGetId() {
        return id;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    @Deprecated
    public void setImage(final String image) {
        this.image = image;
    }

    @Override
    public boolean hasImageEqualTo(final String image) {
        return Objects.equals(this.getImage(), image);
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * @deprecated This will be removed with 7.0.0
     */
    @Deprecated
    @InternalApi
    public void testingOnlySetBeginLine(int i) {
        this.beginLine = i;
    }

    @Override
    public int getBeginColumn() {
        if (beginColumn == -1) {
            if (children != null && children.length > 0) {
                return children[0].getBeginColumn();
            } else {
                throw new RuntimeException("Unable to determine beginning line of Node.");
            }
        } else {
            return beginColumn;
        }
    }

    /**
     * @deprecated This will be removed with 7.0.0
     */
    @Deprecated
    @InternalApi
    public void testingOnlySetBeginColumn(final int i) {
        this.beginColumn = i;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    /**
     * @deprecated This will be removed with 7.0.0
     */
    @Deprecated
    @InternalApi
    public void testingOnlySetEndLine(final int i) {
        this.endLine = i;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * @deprecated This will be removed with 7.0.0
     */
    @Deprecated
    @InternalApi
    public void testingOnlySetEndColumn(final int i) {
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
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }

    @Override
    public Node getNthParent(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        Node result = this.getParent();
        for (int i = 1; i < n; i++) {
            if (result == null) {
                return null;
            }
            result = result.getParent();
        }
        return result;
    }

    @Override
    public <T> T getFirstParentOfType(final Class<T> parentType) {
        Node parentNode = getParent();
        while (parentNode != null && !parentType.isInstance(parentNode)) {
            parentNode = parentNode.getParent();
        }
        return parentType.cast(parentNode);
    }

    @Override
    public <T> List<T> getParentsOfType(final Class<T> parentType) {
        final List<T> parents = new ArrayList<>();
        Node parentNode = getParent();
        while (parentNode != null) {
            if (parentType.isInstance(parentNode)) {
                parents.add(parentType.cast(parentNode));
            }
            parentNode = parentNode.getParent();
        }
        return parents;
    }

    @SafeVarargs
    @Override
    public final <T> T getFirstParentOfAnyType(final Class<? extends T>... parentTypes) {
        Node parentNode = getParent();
        while (parentNode != null) {
            for (final Class<? extends T> c : parentTypes) {
                if (c.isInstance(parentNode)) {
                    return c.cast(parentNode);
                }
            }
            parentNode = parentNode.getParent();
        }
        return null;
    }

    @Override
    public <T> List<T> findDescendantsOfType(final Class<T> targetType) {
        final List<T> list = new ArrayList<>();
        findDescendantsOfType(this, targetType, list, false);
        return list;
    }

    @Override
    public <T> List<T> findDescendantsOfType(final Class<T> targetType, final boolean crossBoundaries) {
        final List<T> list = new ArrayList<>();
        findDescendantsOfType(this, targetType, list, crossBoundaries);
        return list;
    }

    /**
    * @deprecated Use {@link #findDescendantsOfType(Class, boolean)} instead, which
    * returns a result list.
    */
    @Deprecated
    @Override
    public <T> void findDescendantsOfType(final Class<T> targetType, final List<T> results,
                                          final boolean crossBoundaries) {
        findDescendantsOfType(this, targetType, results, crossBoundaries);
    }

    private static <T> void findDescendantsOfType(final Node node, final Class<T> targetType, final List<T> results,
                                                  final boolean crossFindBoundaries) {

        for (Node child : node.children()) {
            if (targetType.isAssignableFrom(child.getClass())) {
                results.add(targetType.cast(child));
            }

            if (crossFindBoundaries || !child.isFindBoundary()) {
                findDescendantsOfType(child, targetType, results, crossFindBoundaries);
            }
        }
    }

    @Override
    public <T> List<T> findChildrenOfType(final Class<T> targetType) {
        final List<T> list = new ArrayList<>();
        for (Node child : children()) {
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
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document document = db.newDocument();
            appendElement(document);
            return document;
        } catch (final ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }

    protected void appendElement(final org.w3c.dom.Node parentNode) {
        final DocumentNavigator docNav = new DocumentNavigator();
        Document ownerDocument = parentNode.getOwnerDocument();
        if (ownerDocument == null) {
            // If the parentNode is a Document itself, it's ownerDocument is
            // null
            ownerDocument = (Document) parentNode;
        }
        final String elementName = docNav.getElementName(this);
        final Element element = ownerDocument.createElement(elementName);
        parentNode.appendChild(element);
        for (final Iterator<Attribute> iter = docNav.getAttributeAxisIterator(this); iter.hasNext();) {
            final Attribute attr = iter.next();
            element.setAttribute(attr.getName(), attr.getStringValue());
        }
        for (final Iterator<Node> iter = docNav.getChildAxisIterator(this); iter.hasNext();) {
            final AbstractNode child = (AbstractNode) iter.next();
            child.appendElement(element);
        }
    }

    @Override
    public <T> T getFirstDescendantOfType(final Class<T> descendantType) {
        return getFirstDescendantOfType(descendantType, this);
    }

    @Override
    public <T> T getFirstChildOfType(final Class<T> childType) {
        for (Node child : children()) {
            if (childType.isInstance(child)) {
                return childType.cast(child);
            }
        }
        return null;
    }

    private static <T> T getFirstDescendantOfType(final Class<T> descendantType, final Node node) {
        for (Node n1 : node.children()) {
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
    public final <T> boolean hasDescendantOfType(final Class<T> type) {
        return getFirstDescendantOfType(type) != null;
    }

    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     * @deprecated Use {@link #hasDescendantOfAnyType(Class[])}
     */
    @Deprecated
    public final boolean hasDecendantOfAnyType(final Class<?>... types) {
        return hasDescendantOfAnyType(types);
    }

    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     */
    public final boolean hasDescendantOfAnyType(final Class<?>... types) {
        // TODO consider implementing that with a single traversal!
        // hasDescendantOfType could then be a special case of this one
        // But to really share implementations, getFirstDescendantOfType's
        // internal helper could have to give up some type safety to rely
        // instead on a getFirstDescendantOfAnyType, then cast to the correct type
        for (final Class<?> type : types) {
            if (hasDescendantOfType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Node> findChildNodesWithXPath(final String xpathString) throws JaxenException {
        return new BaseXPath(xpathString, new DocumentNavigator()).selectNodes(this);
    }

    @Override
    public boolean hasDescendantMatchingXPath(final String xpathString) {
        try {
            return !findChildNodesWithXPath(xpathString).isEmpty();
        } catch (final JaxenException e) {
            throw new RuntimeException("XPath expression " + xpathString + " failed: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    /**
     * @deprecated Not all nodes are based on tokens, and this is an implementation detail
     */
    @Deprecated
    public GenericToken jjtGetFirstToken() {
        return firstToken;
    }

    /**
     * @deprecated This is JJTree-specific and will be removed from this superclass.
     */
    @Deprecated
    public void jjtSetFirstToken(final GenericToken token) {
        this.firstToken = token;
    }

    /**
     * @deprecated Not all nodes are based on tokens, and this is an implementation detail
     */
    @Deprecated
    public GenericToken jjtGetLastToken() {
        return lastToken;
    }

    /**
     * @deprecated This is JJTree-specific and will be removed from this superclass.
     */
    @Deprecated
    public void jjtSetLastToken(final GenericToken token) {
        this.lastToken = token;
    }


    @Override
    public Iterable<? extends Node> children() {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return childrenIterator(AbstractNode.this);
            }
        };
    }


    private static Iterator<Node> childrenIterator(final Node parent) {
        assert parent != null : "parent should not be null";

        final int numChildren = parent.getNumChildren();
        if (numChildren == 0) {
            return Collections.emptyIterator();
        }

        return new Iterator<Node>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numChildren;
            }

            @Override
            public Node next() {
                return parent.getChild(i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove");
            }
        };
    }

    /**
     * @deprecated This is internal API
     */
    @Deprecated
    @InternalApi
    @Override
    public void remove() {
        // Detach current node of its parent, if any
        final Node parent = getParent();
        if (parent != null) {
            parent.removeChildAtIndex(getIndexInParent());
            jjtSetParent(null);
        }

        // TODO [autofix]: Notify action for handling text edition
    }

    /**
     * @deprecated This is internal API
     */
    @Deprecated
    @InternalApi
    @Override
    public void removeChildAtIndex(final int childIndex) {
        if (0 <= childIndex && childIndex < getNumChildren()) {
            // Remove the child at the given index
            children = ArrayUtils.remove(children, childIndex);
            // Update the remaining & left-shifted children indexes
            for (int i = childIndex; i < getNumChildren(); i++) {
                getChild(i).jjtSetChildIndex(i);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This default implementation adds compatibility with the previous
     * way to get the xpath node name, which used {@link Object#toString()}.
     * <p>
     * <p>Please override it. It will be removed in version 7.0.0.
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
     * @deprecated The equivalence between toString and a node's name could be broken as soon as release 7.0.0.
     * Use getXPathNodeName for that purpose. The use for debugging purposes is not deprecated.
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
