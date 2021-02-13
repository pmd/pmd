/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;


/**
 * Root interface for all AST nodes. This interface provides only the API
 * shared by all AST implementations in PMD language modules. This includes for now:
 * <ul>
 * <li>Tree traversal methods: {@link #getParent()}, {@link #getIndexInParent()},
 * {@link #getChild(int)}, and {@link #getNumChildren()}. These four basic
 * operations are used to implement more specific traversal operations,
 * like {@link #firstChild(Class)}, and {@link NodeStream}s.
 * <li>The API used to describe nodes in a form understandable by XPath expressions:
 * {@link #getXPathNodeName()},  {@link #getXPathAttributesIterator()}
 * <li>Location metadata: eg {@link #getBeginLine()}, {@link #getBeginColumn()}
 * <li>An extensible metadata store: {@link #getUserMap()}
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
 */
public interface Node {

    Comparator<Node> COORDS_COMPARATOR =
        Comparator.comparingInt(Node::getBeginLine)
                  .thenComparingInt(Node::getBeginColumn)
                  .thenComparingInt(Node::getEndLine)
                  .thenComparingInt(Node::getEndColumn);

    /**
     * Returns a string token, usually filled-in by the parser, which describes some textual characteristic of this
     * node. This is usually an identifier, but you should check that using the Designer. On most nodes though, this
     * method returns {@code null}.
     *
     * @deprecated Should be replaced with methods that have more specific
     *     names in node classes.
     */
    @Deprecated
    @DeprecatedUntil700
    default String getImage() {
        return null;
    }


    /**
     * Returns true if this node's image is equal to the given string.
     *
     * @param image The image to check
     *
     * @deprecated See {@link #getImage()}
     */
    @Deprecated
    @DeprecatedUntil700
    default boolean hasImageEqualTo(String image) {
        return Objects.equals(getImage(), image);
    }

    /**
     * Compare the coordinates of this node with the other one as if
     * with {@link #COORDS_COMPARATOR}. The result is useless
     * if both nodes are not from the same tree.
     *
     * @param other Other node
     *
     * @return A positive integer if this node comes AFTER the other,
     *     0 if they have the same position, a negative integer if this
     *     node comes BEFORE the other
     */
    default int compareLocation(Node other) {
        return COORDS_COMPARATOR.compare(this, other);
    }

    int getBeginLine();


    int getBeginColumn();


    int getEndLine();


    // FIXME should not be inclusive
    int getEndColumn();


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
    @NoAttribute
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
     *
     * @deprecated Use node stream methods: {@code node.ancestors().get(n-1)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Node getNthParent(int n) {
        return ancestors().get(n - 1);
    }

    /**
     * Traverses up the tree to find the first parent instance of type parentType or one of its subclasses.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T> The type you want to find
     * @return Node of type parentType. Returns null if none found.
     *
     * @deprecated Use node stream methods: {@code node.ancestors(parentType).first()}
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> T getFirstParentOfType(Class<? extends T> parentType) {
        return this.<T>ancestors(parentType).first();
    }

    /**
     * Traverses up the tree to find all of the parent instances of type parentType or one of its subclasses. The nodes
     * are ordered deepest-first.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T> The type you want to find
     * @return List of parentType instances found.
     *
     * @deprecated Use node stream methods: {@code node.ancestors(parentType).toList()}.
     *     Most usages don't really need a list though, eg you can iterate the node stream instead
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> List<T> getParentsOfType(Class<? extends T> parentType) {
        return this.<T>ancestors(parentType).toList();
    }


    /**
     * Traverses the children to find all the instances of type childType or one of its subclasses.
     *
     * @param childType class which you want to find.
     * @return List of all children of type childType. Returns an empty list if none found.
     * @see #findDescendantsOfType(Class) if traversal of the entire tree is needed.
     *
     * @deprecated Use node stream methods: {@code node.children(childType).toList()}.
     *     Most usages don't really need a list though, eg you can iterate the node stream instead
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> List<T> findChildrenOfType(Class<? extends T> childType) {
        return this.<T>children(childType).toList();
    }


    /**
     * Traverses down the tree to find all the descendant instances of type descendantType without crossing find
     * boundaries.
     *
     * @param targetType class which you want to find.
     * @return List of all children of type targetType. Returns an empty list if none found.
     *
     * @deprecated Use node stream methods: {@code node.descendants(targetType).toList()}.
     *     Most usages don't really need a list though, eg you can iterate the node stream instead
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> List<T> findDescendantsOfType(Class<? extends T> targetType) {
        return this.<T>descendants(targetType).toList();
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
     *
     * @deprecated Use node stream methods: {@code node.descendants(targetType).crossFindBoundaries(b).toList()}.
     *     Most usages don't really need a list though, eg you can iterate the node stream instead
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> List<T> findDescendantsOfType(Class<? extends T> targetType, boolean crossFindBoundaries) {
        return this.<T>descendants(targetType).crossFindBoundaries(crossFindBoundaries).toList();
    }

    /**
     * Traverses the children to find the first instance of type childType.
     *
     * @param childType class which you want to find.
     * @return Node of type childType. Returns <code>null</code> if none found.
     * @see #getFirstDescendantOfType(Class) if traversal of the entire tree is needed.
     *
     * @deprecated Use {@link #firstChild(Class)}
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> T getFirstChildOfType(Class<? extends T> childType) {
        return firstChild(childType);
    }


    /**
     * Traverses down the tree to find the first descendant instance of type descendantType without crossing find
     * boundaries.
     *
     * @param descendantType class which you want to find.
     * @return Node of type descendantType. Returns <code>null</code> if none found.
     *
     * @deprecated Use node stream methods: {@code node.descendants(targetType).first()}.
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> T getFirstDescendantOfType(Class<? extends T> descendantType) {
        return descendants(descendantType).first();
    }

    /**
     * Finds if this node contains a descendant of the given type without crossing find boundaries.
     *
     * @param type the node type to search
     * @return <code>true</code> if there is at least one descendant of the given type
     *
     * @deprecated Use node stream methods: {@code node.descendants(targetType).nonEmpty()}.
     */
    @Deprecated
    @DeprecatedUntil700
    default <T extends Node> boolean hasDescendantOfType(Class<? extends T> type) {
        return descendants(type).nonEmpty();
    }

    /**
     * Returns all the nodes matching the xpath expression.
     *
     * @param xpathString the expression to check
     * @return List of all matching nodes. Returns an empty list if none found.
     * @deprecated This is very inefficient and should not be used in new code. PMD 7.0.0 will remove
     *             support for this method.
     */
    @Deprecated
    default List<Node> findChildNodesWithXPath(String xpathString) {
        return new SaxonXPathRuleQuery(
            xpathString,
            XPathVersion.DEFAULT,
            Collections.emptyMap(),
            XPathHandler.noFunctionDefinitions(),
            // since this method will be removed, we don't log anything anymore
            DeprecatedAttrLogger.noop()
        ).evaluate(this);
    }



    /**
     * Returns a data map used to store additional information on this node.
     *
     * @return The user data map of this node
     *
     * @since 6.22.0
     */
    DataMap<DataKey<?, ?>> getUserMap();


    /**
     * Returns the parent of this node, or null if this is the {@linkplain RootNode root}
     * of the tree.
     *
     * @return The parent of this node
     *
     * @since 6.21.0
     */
    Node getParent();


    /**
     * Returns the child of this node at the given index.
     *
     * @throws IndexOutOfBoundsException if the index is negative or greater than {@link #getNumChildren()}.
     * @since 6.21.0
     */
    Node getChild(int index);


    /**
     * Returns the number of children of this node.
     *
     * @since 6.21.0
     */
    int getNumChildren();

    /**
     * Returns the index of this node in its parent's children. If this
     * node is a {@linkplain RootNode root node}, returns -1.
     *
     * @return The index of this node in its parent's children
     *
     * @since 6.21.0
     */
    int getIndexInParent();


    /**
     * Calls back the visitor's visit method corresponding to the runtime
     * type of this Node. This should usually be preferred to calling
     * a {@code visit} method directly (usually the only calls to those
     * are in the implementations of this {@code acceptVisitor} method).
     *
     * @param <R>     Return type of the visitor
     * @param <P>     Parameter type of the visitor
     * @param visitor Visitor to dispatch
     * @param data    Parameter to the visit
     *
     * @return What the visitor returned. If this node doesn't recognize
     * the type of the visitor, returns {@link AstVisitor#cannotVisit(Node, Object) visitor.cannotVisit(this, data)}.
     *
     * @implSpec A typical implementation will check the type of the visitor to
     *     be that of the language specific visitor, then call the most specific
     *     visit method of this Node. This is typically implemented by having
     *     a different override per concrete node class (no shortcuts).
     *
     *     The default implementation calls back {@link AstVisitor#cannotVisit(Node, Object)}.
     *
     * @since 7.0.0
     */
    default <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.cannotVisit(this, data);
    }

    /**
     * Returns the {@link AstInfo} for this root node.
     *
     * @implNote This default implementation can not work unless overridden in the root node.
     */
    default AstInfo<? extends RootNode> getAstInfo() {
        return getRoot().getAstInfo();
    }


    /**
     * Gets the name of the node that is used to match it with XPath queries.
     *
     * @return The XPath node name
     */
    String getXPathNodeName();


    /**
     * Returns an iterator enumerating all the attributes that are available
     * from XPath for this node.
     *
     * @return An attribute iterator for this node
     */
    default Iterator<Attribute> getXPathAttributesIterator() {
        return new AttributeAxisIterator(this);
    }


    /**
     * Returns the first child of this node, or null if it doesn't exist.
     *
     * @since 7.0.0
     */
    default @Nullable Node getFirstChild() {
        return getNumChildren() > 0 ? getChild(0) : null;
    }


    /**
     * Returns the first last of this node, or null if it doesn't exist.
     *
     * @since 7.0.0
     */
    default @Nullable Node getLastChild() {
        return getNumChildren() > 0 ? getChild(getNumChildren() - 1) : null;
    }


    /**
     * Returns the previous sibling of this node, or null if it does not exist.
     *
     * @since 7.0.0
     */
    default @Nullable Node getPreviousSibling() {
        Node parent = getParent();
        int idx = getIndexInParent();
        if (parent != null && idx > 0) {
            return parent.getChild(idx - 1);
        }
        return null;
    }

    /**
     * Returns the next sibling of this node, or null if it does not exist.
     *
     * @since 7.0.0
     */
    default @Nullable Node getNextSibling() {
        Node parent = getParent();
        int idx = getIndexInParent();
        if (parent != null && idx < parent.getNumChildren()) {
            return parent.getChild(idx + 1);
        }
        return null;
    }

    /**
     * Returns a node stream containing only this node.
     * {@link NodeStream#of(Node)} is a null-safe version
     * of this method.
     *
     * @return A node stream containing only this node
     *
     * @see NodeStream#of(Node)
     * @since 7.0.0
     */
    default NodeStream<? extends Node> asStream() {
        return StreamImpl.singleton(this);
    }


    /**
     * Returns a node stream containing all the children of
     * this node. This method does not provide much type safety,
     * you'll probably want to use {@link #children(Class)}.
     *
     * @see NodeStream#children(Class)
     * @since 7.0.0
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
     * @since 7.0.0
     */
    default DescendantNodeStream<? extends Node> descendants() {
        return StreamImpl.descendants(this);
    }


    /**
     * Returns a node stream containing this node, then all its
     * descendants. See {@link DescendantNodeStream} for details.
     *
     * @return A node stream of the whole subtree topped by this node
     *
     * @see NodeStream#descendantsOrSelf()
     * @since 7.0.0
     */
    default DescendantNodeStream<? extends Node> descendantsOrSelf() {
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
     * @since 7.0.0
     */
    default NodeStream<? extends Node> ancestors() {
        return StreamImpl.ancestors(this);

    }


    /**
     * Returns a node stream containing this node and its ancestors.
     * The nodes of the returned stream are yielded in a depth-first fashion.
     *
     * @return A stream of ancestors
     *
     * @see NodeStream#ancestorsOrSelf()
     * @since 7.0.0
     */
    default NodeStream<? extends Node> ancestorsOrSelf() {
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
     * @since 7.0.0
     */
    default <R extends Node> NodeStream<R> children(Class<? extends R> rClass) {
        return StreamImpl.children(this, rClass);
    }

    /**
     * Returns the first child of this node that has the given type.
     * Returns null if no such child exists.
     *
     * <p>If you want to process this element as a node stream, use
     * {@code asStream().firstChild(rClass)} instead, which returns
     * a node stream.
     *
     * @param rClass Type of the child to find
     * @param <R>    Type of the child to find
     *
     * @return A child, or null
     *
     * @since 7.0.0
     */
    default <R extends Node> @Nullable R firstChild(Class<? extends R> rClass) {
        return children(rClass).first();
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
     * @since 7.0.0
     */
    default <R extends Node> DescendantNodeStream<R> descendants(Class<? extends R> rClass) {
        return StreamImpl.descendants(this, rClass);
    }


    /**
     * Returns the {@linkplain #ancestors() ancestor stream} of this node
     * filtered by the given node type.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see NodeStream#ancestors(Class)
     * @since 7.0.0
     */
    default <R extends Node> NodeStream<R> ancestors(Class<? extends R> rClass) {
        return StreamImpl.ancestors(this, rClass);
    }

    /**
     * Returns the root of the tree this node is declared in.
     *
     * @since 7.0.0
     */
    default @NonNull RootNode getRoot() {
        Node r = this;
        while (r.getParent() != null) {
            r = r.getParent();
        }
        if (!(r instanceof RootNode)) {
            throw new AssertionError("Root of the tree should implement RootNode");
        }
        return (RootNode) r;
    }
}
