/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.Reportable;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;


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
 */
public interface Node extends Reportable {

    /**
     * Returns a string token, usually filled-in by the parser, which describes some textual characteristic of this
     * node. This is usually an identifier, but you should check that using the Designer. On most nodes though, this
     * method returns {@code null}.
     */
    default String getImage() {
        return null;
    }


    /**
     * Returns true if this node's image is equal to the given string.
     *
     * @param image The image to check
     */
    default boolean hasImageEqualTo(String image) {
        return Objects.equals(getImage(), image);
    }


    /**
     * {@inheritDoc}
     * This is not necessarily the exact boundaries of the node in the
     * text. Nodes that can provide exact position information do so
     * using a {@link TextRegion}, by implementing {@link TextAvailableNode}.
     *
     * <p>Use this instead of {@link #getBeginColumn()}/{@link #getBeginLine()}, etc.
     */
    @Override
    FileLocation getReportLocation();


    /**
     * Compare the coordinates of this node with the other one as if
     * with {@link FileLocation#COORDS_COMPARATOR}. The result is useless
     * if both nodes are not from the same tree (todo check it?).
     *
     * @param node Other node
     *
     * @return A positive integer if this node comes AFTER the other,
     *     0 if they have the same position, a negative integer if this
     *     node comes BEFORE the other
     */
    default int compareLocation(Node node) {
        return FileLocation.COORDS_COMPARATOR.compare(getReportLocation(), node.getReportLocation());
    }

    // Those are kept here because they're handled specially as XPath
    // attributes

    @Override
    default int getBeginLine() {
        return getReportLocation().getBeginLine();
    }

    @Override
    default int getBeginColumn() {
        return getReportLocation().getBeginColumn();
    }

    @Override
    default int getEndLine() {
        return getReportLocation().getEndLine();
    }

    @Override
    default int getEndColumn() {
        return getReportLocation().getEndColumn();
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
     */
    default <T extends Node> List<T> getParentsOfType(Class<? extends T> parentType) {
        return this.<T>ancestors(parentType).toList();
    }


    /**
     * Traverses the children to find all the instances of type childType or one of its subclasses.
     *
     * @param childType class which you want to find.
     * @return List of all children of type childType. Returns an empty list if none found.
     * @see #findDescendantsOfType(Class) if traversal of the entire tree is needed.
     */
    default <T extends Node> List<T> findChildrenOfType(Class<? extends T> childType) {
        return this.<T>children(childType).toList();
    }


    /**
     * Traverses down the tree to find all the descendant instances of type descendantType without crossing find
     * boundaries.
     *
     * @param targetType class which you want to find.
     * @return List of all children of type targetType. Returns an empty list if none found.
     */
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
     */
    default <T extends Node> List<T> findDescendantsOfType(Class<? extends T> targetType, boolean crossFindBoundaries) {
        return this.<T>descendants(targetType).crossFindBoundaries(crossFindBoundaries).toList();
    }

    /**
     * Traverses the children to find the first instance of type childType.
     *
     * @param childType class which you want to find.
     * @return Node of type childType. Returns <code>null</code> if none found.
     * @see #getFirstDescendantOfType(Class) if traversal of the entire tree is needed.
     */
    default <T extends Node> T getFirstChildOfType(Class<? extends T> childType) {
        return children(childType).first();
    }


    /**
     * Traverses down the tree to find the first descendant instance of type descendantType without crossing find
     * boundaries.
     *
     * @param descendantType class which you want to find.
     * @return Node of type descendantType. Returns <code>null</code> if none found.
     */
    default <T extends Node> T getFirstDescendantOfType(Class<? extends T> descendantType) {
        return descendants(descendantType).first();
    }

    /**
     * Finds if this node contains a descendant of the given type without crossing find boundaries.
     *
     * @param type the node type to search
     * @return <code>true</code> if there is at least one descendant of the given type
     */
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
     */
    DataMap<DataKey<?, ?>> getUserMap();


    /**
     * Returns the text document from which this tree was parsed. This
     * means, that the whole file text is in memory while the AST is.
     *
     * @return The text document
     */
    default @NonNull TextDocument getTextDocument() {
        return getRoot().getTextDocument();
    }

    /**
     * Returns the parent of this node, or null if this is the {@linkplain RootNode root}
     * of the tree.
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
     * @return The index of this node in its parent's children
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
     */
    default <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.cannotVisit(this, data);
    }


    default LanguageVersion getLanguageVersion() {
        return getTextDocument().getLanguageVersion();
    }


    /**
     * @deprecated This is simply a placeholder until we have TextDocuments
     */
    @Deprecated
    @NoAttribute
    default String getSourceCodeFile() {
        return getTextDocument().getDisplayName();
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
     */
    default @Nullable Node getFirstChild() {
        return getNumChildren() > 0 ? getChild(0) : null;
    }


    /**
     * Returns the first last of this node, or null if it doesn't exist.
     */
    default @Nullable Node getLastChild() {
        return getNumChildren() > 0 ? getChild(getNumChildren() - 1) : null;
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
    default NodeStream<? extends Node> asStream() {
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
     */
    default <R extends Node> NodeStream<R> children(Class<? extends R> rClass) {
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
    default <R extends Node> DescendantNodeStream<R> descendants(Class<? extends R> rClass) {
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
    default <R extends Node> NodeStream<R> ancestors(Class<? extends R> rClass) {
        return StreamImpl.ancestors(this, rClass);
    }

    /**
     * Returns the root of the tree this node is declared in.
     */
    @NonNull
    default RootNode getRoot() {
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
