/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import net.sourceforge.pmd.reporting.Reportable;
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
 * <li>Location metadata: {@link #getReportLocation()}
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
 * are indeed of the same type.
 */
public interface Node extends Reportable {

    /**
     * Compares nodes according to their location in the file.
     * Note that this comparator is not <i>consistent with equals</i>
     * (see {@link Comparator}) as some nodes have the same location.
     */
    Comparator<Node> COORDS_COMPARATOR =
        Comparator.comparing(Node::getReportLocation, FileLocation.COMPARATOR);

    /**
     * Returns a string token, usually filled-in by the parser, which describes some textual characteristic of this
     * node. This is usually an identifier, but you should check that using the Designer. On most nodes though, this
     * method returns {@code null}.
     *
     * <p><strong>Note:</strong>
     * This method will be deprecated in the future (<a href="https://github.com/pmd/pmd/issues/4787">#4787</a>).
     * It will be replaced with methods that have more specific names in node classes. In some cases, there
     * are already alternatives available that should be used.</p>
     */
    // @Deprecated // todo deprecate (#4787)
    default String getImage() {
        return null;
    }


    /**
     * Returns true if this node's image is equal to the given string.
     *
     * <p><strong>Note:</strong>
     * This method will be deprecated in the future (<a href="https://github.com/pmd/pmd/issues/4787">#4787</a>).
     * See {@link #getImage()}.
     * </p>
     * @param image The image to check
     */
    // @Deprecated // todo deprecate (#4787)
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

    /**
     * {@inheritDoc}
     * This is not necessarily the exact boundaries of the node in the
     * text. Nodes that can provide exact position information do so
     * using a {@link TextRegion}, by implementing {@link TextAvailableNode}.
     *
     * <p>Use this instead of {@link #getBeginColumn()}/{@link #getBeginLine()}, etc.
     */
    @Override
    default FileLocation getReportLocation() {
        return getAstInfo().getTextDocument().toLocation(getTextRegion());
    }

    /**
     * Returns a region of text delimiting the node in the underlying
     * text document. This does not necessarily match the
     * {@link #getReportLocation() report location}.
     */
    TextRegion getTextRegion();


    // Those are kept here because they're handled specially as XPath
    // attributes, for now
    // ->  [core] Deprecate XPath attributes for node coordinates (eg @BeginLine) #3876 (https://github.com/pmd/pmd/issues/3876)

    default int getBeginLine() {
        return getReportLocation().getStartLine();
    }

    default int getBeginColumn() {
        return getReportLocation().getStartColumn();
    }

    default int getEndLine() {
        return getReportLocation().getEndLine();
    }

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
     * @return True if this node is a find boundary
     *
     * @see DescendantNodeStream#crossFindBoundaries(boolean)
     */
    @NoAttribute
    default boolean isFindBoundary() {
        return false;
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
     * Returns the text document from which this tree was parsed. This
     * means, that the whole file text is in memory while the AST is.
     *
     * @return The text document
     */
    default @NonNull TextDocument getTextDocument() {
        return getAstInfo().getTextDocument();
    }

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
     *
     * @see AttributeAxisIterator
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
        if (parent != null && idx + 1 < parent.getNumChildren()) {
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

    /**
     * Returns the language version of this node.
     */
    default LanguageVersion getLanguageVersion() {
        return getTextDocument().getLanguageVersion();
    }
}
