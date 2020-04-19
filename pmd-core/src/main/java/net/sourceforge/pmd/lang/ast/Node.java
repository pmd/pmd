/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.ast.xpath.internal.ContextualizedNavigator;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;


/**
 * Root interface for all AST nodes. This interface provides only the API
 * shared by all AST implementations in PMD language modules. This includes for now:
 * <ul>
 * <li>Tree traversal methods, like {@link #getParent()} and {@link #getFirstChildOfType(Class)}
 * <li>The API used to describe nodes in a form understandable by XPath expressions:
 * {@link #getXPathNodeName()},  {@link #getXPathAttributesIterator()}
 * <li>Location metadata: eg {@link #getBeginLine()}, {@link #getBeginColumn()}
 * </ul>
 *
 * <p>Every language implementation must publish a sub-interface of Node
 * which serves as a supertype for all nodes of that language (e.g.
 * pmd-java provides JavaNode, pmd-apex provides ApexNode, etc.). It is
 * assumed in many places that the {@link #getChild(int)} and {@link #getParent()}
 * method return an instance of this sub-interface. For example,
 * no JSP node should have a Java node as its child. Embedding nodes from
 * different languages will not be done via these methods, and conforming
 * implementations should ensure that every node returned by these methods
 * are indeed of the same type. Possibly, a type parameter will be added to
 * the Node interface in 7.0.0 to enforce it at compile-time.
 *
 * <p>A number of methods are deprecated and will be removed in 7.0.0.
 * Most of them are implementation details that clutter this API and
 * make implementation more difficult. Some methods prefixed with {@code jjt}
 * have a more conventional counterpart (e.g. {@link #jjtGetParent()} and
 * {@link #getParent()}) that should be preferred.
 */
public interface Node {

    // COMMENT: is it ok to take the opportunity on PMD 7 to rename this API and take out of there the methods
    // that are only needed for javaCC implementations?


    /**
     * This method is called after the node has been made the current node. It
     * indicates that child nodes can now be added to it.
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    default void jjtOpen() {
        // do nothing
    }


    /**
     * This method is called after all the child nodes have been added.
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    default void jjtClose() {
        // do nothing
    }


    /**
     * Sets the parent of this node.
     *
     * @param parent The parent
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    default void jjtSetParent(Node parent) {
        throw new UnsupportedOperationException("JJTree specific");
    }


    /**
     * Returns the parent of this node.
     *
     * @return The parent of the node
     *
     * @deprecated Use {@link #getParent()}
     */
    @Deprecated
    @Nullable
    default Node jjtGetParent() {
        return getParent();
    }


    /**
     * This method tells the node to add its argument to the node's list of children.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    default void jjtAddChild(Node child, int index) {
        throw new UnsupportedOperationException("JJTree specific");
    }


    /**
     * Sets the index of this node from the perspective of its parent. This
     * means: this.getParent().getChild(index) == this.
     *
     * @param index the child index
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    default void jjtSetChildIndex(int index) {
        throw new UnsupportedOperationException("JJTree specific");
    }


    /**
     * This method returns a child node. The children are numbered from zero, left to right.
     *
     * @param index the child index. Must be nonnegative and less than
     *              {@link #jjtGetNumChildren}.
     *
     * @deprecated Use {@link #getChild(int)}
     */
    @Deprecated
    default Node jjtGetChild(int index) {
        return getChild(index);
    }


    /**
     * Returns the number of children the node has.
     *
     * @deprecated Use {@link #getNumChildren()}
     */
    @Deprecated
    default int jjtGetNumChildren() {
        return getNumChildren();
    }


    /**
     * @deprecated This is JJTree-specific and will be removed from this interface.
     */
    @Deprecated
    default int jjtGetId() {
        throw new UnsupportedOperationException("JJTree specific");
    }


    /**
     * Returns a string token, usually filled-in by the parser, which describes some textual characteristic of this
     * node. This is usually an identifier, but you should check that using the Designer. On most nodes though, this
     * method returns {@code null}.
     */
    default String getImage() {
        return null;
    }


    /**
     * @deprecated This is internal API, the image should never be set by developers.
     */
    @InternalApi
    @Deprecated
    default void setImage(String image) {
        throw new UnsupportedOperationException("setImage");
    }


    /**
     * Returns true if this node's image is equal to the given string.
     *
     * @param image The image to check
     */
    default boolean hasImageEqualTo(String image) {
        return getImage() != null && getImage().equals(image);
    }


    int getBeginLine();


    int getBeginColumn();


    int getEndLine();


    // FIXME should not be inclusive
    int getEndColumn();


    /**
     * @deprecated This is Java-specific and will be removed from this interface
     */
    @Deprecated
    default DataFlowNode getDataFlowNode() {
        throw new UnsupportedOperationException("JJTree specific");
    }


    /**
     * @deprecated This is Java-specific and will be removed from this interface
     */
    @Deprecated
    default void setDataFlowNode(DataFlowNode dataFlowNode) {
        throw new UnsupportedOperationException("JJTree specific");
    }



    /**
     * Returns true if this node is considered a boundary by traversal
     * methods. Traversal methods such as {@link #descendants()}
     * don't look past such boundaries by default, which is usually the
     * expected thing to do. For example, in Java, lambdas and nested
     * classes are considered find boundaries.
     *
     * <p>Note: This attribute is deprecated for XPath queries. It is not useful
     * for XPath queries and will be removed with PMD 7.0.0.
     *
     * @return True if this node is a find boundary
     *
     * @see DescendantNodeStream#crossFindBoundaries(boolean)
     */
    @DeprecatedAttribute
    default boolean isFindBoundary() {
        return false;
    }

    /**
     * Returns the n-th parent or null if there are less than {@code n} ancestors.
     *
     * <pre>{@code
     *    getNthParent(1) == jjtGetParent
     * }</pre>
     *
     * @param n how many ancestors to iterate over.
     * @return the n-th parent or null.
     * @throws IllegalArgumentException if {@code n} is negative or zero.
     */
    default Node getNthParent(int n) {
        return ancestors().get(n - 1);
    }

    /**
     * Traverses up the tree to find the first parent instance of type parentType or one of its subclasses.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T> The type you want to find
     * @return Node of type parentType. Returns null if none found.
     */
    default <T extends Node> T getFirstParentOfType(Class<T> parentType) {
        return ancestors(parentType).first();
    }

    /**
     * Traverses up the tree to find all of the parent instances of type parentType or one of its subclasses. The nodes
     * are ordered deepest-first.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T> The type you want to find
     * @return List of parentType instances found.
     */
    default <T extends Node> List<T> getParentsOfType(Class<T> parentType) {
        return ancestors(parentType).toList();
    }

    /**
     * Gets the first parent that's an instance of any of the given types.
     *
     * @param parentTypes Types to look for
     * @param <T> Most specific common type of the parameters
     * @return The first parent with a matching type. Returns null if there is no such parent
     */
    default <T extends Node> T getFirstParentOfAnyType(Class<? extends T>... parentTypes) {
        return ancestors().map(it -> {
            for (final Class<? extends T> c : parentTypes) {
                if (c.isInstance(it)) {
                    return c.cast(it);
                }
            }
            return null;
        }).first();
    }

    /**
     * Traverses the children to find all the instances of type childType or one of its subclasses.
     *
     * @param childType class which you want to find.
     * @return List of all children of type childType. Returns an empty list if none found.
     * @see #findDescendantsOfType(Class) if traversal of the entire tree is needed.
     */
    default <T extends Node> List<T> findChildrenOfType(Class<T> childType) {
        return children(childType).toList();
    }


    /**
     * Traverses down the tree to find all the descendant instances of type descendantType without crossing find
     * boundaries.
     *
     * @param targetType class which you want to find.
     * @return List of all children of type targetType. Returns an empty list if none found.
     */
    default <T extends Node> List<T> findDescendantsOfType(Class<T> targetType) {
        return descendants(targetType).toList();
    }

    /**
     * Traverses down the tree to find all the descendant instances of type descendantType.
     *
     * @param targetType class which you want to find.
     * @param results list to store the matching descendants
     * @param crossFindBoundaries if <code>false</code>, recursion stops for nodes for which {@link #isFindBoundary()}
     * is <code>true</code>
     * @deprecated Use {@link #findDescendantsOfType(Class, boolean)} instead, which
     * returns a result list.
     */
    @Deprecated
    default <T extends Node> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossFindBoundaries) {
        descendants(targetType).crossFindBoundaries(crossFindBoundaries).forEach(results::add);
    }

    /**
     * Traverses down the tree to find all the descendant instances of type
     * descendantType.
     *
     * @param targetType
     *            class which you want to find.
     * @param crossFindBoundaries
     *            if <code>false</code>, recursion stops for nodes for which
     *            {@link #isFindBoundary()} is <code>true</code>
     * @return List of all matching descendants
     */
    default <T extends Node> List<T> findDescendantsOfType(Class<T> targetType, boolean crossFindBoundaries) {
        return descendants(targetType).crossFindBoundaries(crossFindBoundaries).toList();
    }

    /**
     * Traverses the children to find the first instance of type childType.
     *
     * @param childType class which you want to find.
     * @return Node of type childType. Returns <code>null</code> if none found.
     * @see #getFirstDescendantOfType(Class) if traversal of the entire tree is needed.
     */
    default <T extends Node> T getFirstChildOfType(Class<T> childType) {
        return children(childType).first();
    }

    /**
     * Traverses down the tree to find the first descendant instance of type descendantType without crossing find
     * boundaries.
     *
     * @param descendantType class which you want to find.
     * @return Node of type descendantType. Returns <code>null</code> if none found.
     */
    default <T extends Node> T getFirstDescendantOfType(Class<T> descendantType) {
        return descendants(descendantType).first();
    }

    /**
     * Finds if this node contains a descendant of the given type without crossing find boundaries.
     *
     * @param type the node type to search
     * @return <code>true</code> if there is at least one descendant of the given type
     */
    default <T extends Node> boolean hasDescendantOfType(Class<T> type) {
        return descendants(type).nonEmpty();
    }

    /**
     * Returns all the nodes matching the xpath expression.
     *
     * @param xpathString the expression to check
     * @return List of all matching nodes. Returns an empty list if none found.
     * @throws JaxenException if the xpath is incorrect or fails altogether
     *
     * @deprecated This is very inefficient and should not be used in new code. PMD 7.0.0 will remove
     *             support for this method.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    default List<Node> findChildNodesWithXPath(String xpathString) throws JaxenException {
        return new BaseXPath(xpathString, new ContextualizedNavigator(DeprecatedAttrLogger.createAdHocLogger()))
                .selectNodes(this);
    }

    /**
     * Checks whether at least one descendant matches the xpath expression.
     *
     * @param xpathString the expression to check
     * @return true if there is a match
     *
     * @deprecated This is very inefficient and should not be used in new code. PMD 7.0.0 will remove
     *             support for this method.
     */
    @Deprecated
    default boolean hasDescendantMatchingXPath(String xpathString) {
        try {
            return !findChildNodesWithXPath(xpathString).isEmpty();
        } catch (final JaxenException e) {
            throw new RuntimeException("XPath expression " + xpathString + " failed: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Get a DOM Document which contains Elements and Attributes representative of this Node and it's children.
     * Essentially a DOM tree representation of the Node AST, thereby allowing tools which can operate upon DOM to also
     * indirectly operate on the AST.
     */
    default Document getAsDocument() {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document document = db.newDocument();
            DocumentUtils.appendElement(this, document);
            return document;
        } catch (final ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }

    /**
     * Get the user data associated with this node. By default there is no data, unless it has been set via {@link
     * #setUserData(Object)}.
     *
     * @return The user data set on this node.
     * @deprecated Use {@link #getUserMap()}
     */
    @Deprecated
    Object getUserData();

    /**
     * Set the user data associated with this node.
     * <p>
     * <p>PMD itself will never set user data onto a node. Nor should any Rule
     * implementation, as the AST nodes are shared between concurrently executing Rules (i.e. it is <strong>not</strong>
     * thread-safe).
     * <p>
     * <p>This API is most useful for external applications looking to leverage
     * PMD's robust support for AST structures, in which case application specific annotations on the AST nodes can be
     * quite useful.
     *
     * @param userData The data to set on this node.
     * @deprecated Use {@link #getUserMap()}
     */
    @Deprecated
    void setUserData(Object userData);


    /**
     * Remove the current node from its parent.
     *
     * @deprecated This is internal API and will be removed from this interface with 7.0.0
     */
    @Deprecated
    @InternalApi
    default void remove() {
        throw new UnsupportedOperationException();
    }


    /**
     * This method tells the node to remove the child node at the given index from the node's list of children, if any;
     * if not, no changes are done.
     *
     * @param childIndex The index of the child to be removed
     *
     * @deprecated This is internal API and will be removed from this interface with 7.0.0
     */
    @Deprecated
    @InternalApi
    default void removeChildAtIndex(int childIndex) {
        throw new UnsupportedOperationException();
    }


    /**
     * Returns a data map used to store additional information on this node.
     * This replaces the legacy {@link #getUserData()}/{@link #setUserData(Object)}.
     *
     * @return The user data map of this node
     */
    DataMap<DataKey<?, ?>> getUserMap();

    /**
     * Returns the parent of this node, or null if this is the {@linkplain RootNode root}
     * of the tree.
     *
     * <p>This method should be preferred to {@link #jjtGetParent()}.
     *
     * @return The parent of this node
     */
    Node getParent();

    /**
     * Returns the child of this node at the given index.
     *
     * @throws IndexOutOfBoundsException if the index is negative or greater than {@link #getNumChildren()}.
     */
    Node getChild(int index);


    /**
     * Returns the number of children of this node.
     */
    int getNumChildren();

    /**
     * Returns the index of this node in its parent's children. If this
     * node is a {@linkplain RootNode root node}, returns -1.
     *
     * <p>This method replaces {@link #jjtGetChildIndex()}, whose name was
     * JJTree-specific.
     *
     * @return The index of this node in its parent's children
     */
    int getIndexInParent();

    /**
     * Gets the name of the node that is used to match it with XPath queries.
     *
     * @return The XPath node name
     */
    String getXPathNodeName();

    /**
     * Returns an iterator enumerating all the attributes that are available from XPath for this node.
     *
     * @return An attribute iterator for this node
     */
    default Iterator<Attribute> getXPathAttributesIterator() {
        return new AttributeAxisIterator(this);
    }


    /**
     * Returns a node stream containing only this node.
     * {@link NodeStream#of(Node)} is a null-safe version
     * of this method.
     *
     * @return A node stream containing only this node
     *
     * @see NodeStream#of(Node)
     */
    default NodeStream<Node> asStream() {
        return StreamImpl.singleton(this);
    }


    /**
     * Returns a node stream containing all the children of
     * this node. This method does not provide much type safety,
     * you'll probably want to use {@link #children(Class)}.
     *
     * @see NodeStream#children(Class)
     */
    default NodeStream<? extends Node> children() {
        return StreamImpl.children(this);
    }


    /**
     * Returns a node stream containing all the descendants
     * of this node. See {@link DescendantNodeStream} for details.
     *
     * @return A node stream of the descendants of this node
     *
     * @see NodeStream#descendants()
     */
    default DescendantNodeStream<Node> descendants() {
        return StreamImpl.descendants(this);
    }


    /**
     * Returns a node stream containing this node, then all its
     * descendants. See {@link DescendantNodeStream} for details.
     *
     * @return A node stream of the whole subtree topped by this node
     *
     * @see NodeStream#descendantsOrSelf()
     */
    default DescendantNodeStream<Node> descendantsOrSelf() {
        return StreamImpl.descendantsOrSelf(this);
    }


    /**
     * Returns a node stream containing all the strict ancestors of this node,
     * in innermost to outermost order. The returned stream doesn't contain this
     * node, and is empty if this node has no parent.
     *
     * @return A node stream of the ancestors of this node
     *
     * @see NodeStream#ancestors()
     */
    default NodeStream<Node> ancestors() {
        return StreamImpl.ancestors(this);

    }


    /**
     * Returns a node stream containing this node and its ancestors.
     * The nodes of the returned stream are yielded in a depth-first fashion.
     *
     * @return A stream of ancestors
     *
     * @see NodeStream#ancestorsOrSelf()
     */
    default NodeStream<Node> ancestorsOrSelf() {
        return StreamImpl.ancestorsOrSelf(this);
    }


    /**
     * Returns a {@linkplain NodeStream node stream} of the {@linkplain #children() children}
     * of this node that are of the given type.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see NodeStream#children(Class)
     */
    default <R extends Node> NodeStream<R> children(Class<R> rClass) {
        return StreamImpl.children(this, rClass);
    }


    /**
     * Returns a {@linkplain NodeStream node stream} of the {@linkplain #descendants() descendants}
     * of this node that are of the given type. See {@link DescendantNodeStream}
     * for details.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see NodeStream#descendants(Class)
     */
    default <R extends Node> DescendantNodeStream<R> descendants(Class<R> rClass) {
        return StreamImpl.descendants(this, rClass);
    }


    /**
     * Returns the {@linkplain #ancestors() ancestor stream} of each node
     * in this stream, filtered by the given node type.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see NodeStream#ancestors(Class)
     */
    default <R extends Node> NodeStream<R> ancestors(Class<R> rClass) {
        return StreamImpl.ancestors(this, rClass);
    }

    @NonNull
    default RootNode getRoot() {
        Node r = this;
        while (r != null && !(r instanceof RootNode)) {
            r = r.getParent();
        }
        if (r == null) {
            throw new IllegalStateException("No root node in tree ?");
        }
        return (RootNode) r;
    }
}
