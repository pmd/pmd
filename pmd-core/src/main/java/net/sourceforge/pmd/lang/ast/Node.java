/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.w3c.dom.Document;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

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

    /**
     * This method is called after the node has been made the current node. It
     * indicates that child nodes can now be added to it.
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    void jjtOpen();


    /**
     * This method is called after all the child nodes have been added.
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    void jjtClose();


    /**
     * Sets the parent of this node.
     *
     * @param parent The parent
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    void jjtSetParent(Node parent);


    /**
     * Returns the parent of this node.
     *
     * @return The parent of the node
     *
     * @deprecated Use {@link #getParent()}
     */
    @Deprecated
    Node jjtGetParent();


    /**
     * This method tells the node to add its argument to the node's list of
     * children.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    void jjtAddChild(Node child, int index);

    /**
     * Sets the index of this node from the perspective of its parent. This
     * means: this.getParent().getChild(index) == this.
     *
     * @param index
     *            the child index
     *
     * @deprecated This is JJTree-specific and will be removed from this interface
     */
    @Deprecated
    void jjtSetChildIndex(int index);


    /**
     * Gets the index of this node in the children of its parent.
     *
     * @return The index of the node
     *
     * @deprecated Use {@link #getIndexInParent()}
     */
    @Deprecated
    int jjtGetChildIndex();


    /**
     * This method returns a child node. The children are numbered from zero,
     * left to right.
     *
     * @param index
     *            the child index. Must be nonnegative and less than
     *            {@link #jjtGetNumChildren}.
     *
     * @deprecated Use {@link #getChild(int)}
     */
    @Deprecated
    Node jjtGetChild(int index);


    /**
     * Returns the number of children the node has.
     *
     * @deprecated Use {@link #getNumChildren()}
     */
    @Deprecated
    int jjtGetNumChildren();


    /**
     * @deprecated This is JJTree-specific and will be removed from this interface.
     */
    @Deprecated
    int jjtGetId();


    /**
     * Returns a string token, usually filled-in by the parser, which describes some textual
     * characteristic of this node. This is usually an identifier, but you should check that
     * using the Designer. On most nodes though, this method returns {@code null}.
     */
    String getImage();


    /**
     * @deprecated This is internal API, the image should never be set by developers.
     */
    @InternalApi
    @Deprecated
    void setImage(String image);

    /**
     * Returns true if this node's image is equal to the given string.
     *
     * @param image The image to check
     */
    boolean hasImageEqualTo(String image);

    int getBeginLine();

    int getBeginColumn();

    int getEndLine();

    // FIXME should not be inclusive
    int getEndColumn();


    /**
     * @deprecated This is Java-specific and will be removed from this interface
     */
    @Deprecated
    DataFlowNode getDataFlowNode();


    /**
     * @deprecated This is Java-specific and will be removed from this interface
     */
    @Deprecated
    void setDataFlowNode(DataFlowNode dataFlowNode);


    /**
     * Returns true if this node is considered a boundary by traversal methods.
     * Traversal methods such as {@link #getFirstDescendantOfType(Class)} don't
     * look past such boundaries by default, which is usually the expected thing
     * to do. For example, in Java, lambdas and nested classes are considered
     * find boundaries.
     *
     * <p>Note: This attribute is deprecated for XPath queries. It is not useful
     * for XPath queries and will be removed with PMD 7.0.0.
     */
    @DeprecatedAttribute
    boolean isFindBoundary();


    /**
     * Returns the n-th parent or null if there are less than {@code n} ancestors.
     *
     * @param n how many ancestors to iterate over.
     *
     * @return the n-th parent or null.
     *
     * @throws IllegalArgumentException if {@code n} is negative or zero.
     */
    Node getNthParent(int n);


    /**
     * Traverses up the tree to find the first parent instance of type
     * parentType or one of its subclasses.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T>        The type you want to find
     *
     * @return Node of type parentType. Returns null if none found.
     */
    <T> T getFirstParentOfType(Class<T> parentType);


    /**
     * Traverses up the tree to find all of the parent instances of type
     * parentType or one of its subclasses. The nodes are ordered
     * deepest-first.
     *
     * @param parentType Class literal of the type you want to find
     * @param <T>        The type you want to find
     *
     * @return List of parentType instances found.
     */
    <T> List<T> getParentsOfType(Class<T> parentType);


    /**
     * Gets the first parent that's an instance of any of the given types.
     *
     * @param parentTypes Types to look for
     * @param <T>         Most specific common type of the parameters
     *
     * @return The first parent with a matching type. Returns null if there
     * is no such parent
     */
    <T> T getFirstParentOfAnyType(Class<? extends T>... parentTypes);

    /**
     * Traverses the children to find all the instances of type childType or
     * one of its subclasses.
     *
     * @see #findDescendantsOfType(Class) if traversal of the entire tree is
     *      needed.
     *
     * @param childType
     *            class which you want to find.
     * @return List of all children of type childType. Returns an empty list if
     *         none found.
     */
    <T> List<T> findChildrenOfType(Class<T> childType);

    /**
     * Traverses down the tree to find all the descendant instances of type
     * descendantType without crossing find boundaries.
     *
     * @param targetType
     *            class which you want to find.
     * @return List of all children of type targetType. Returns an empty list if
     *         none found.
     */
    <T> List<T> findDescendantsOfType(Class<T> targetType);

    /**
     * Traverses down the tree to find all the descendant instances of type
     * descendantType.
     *
     * @param targetType
     *            class which you want to find.
     * @param results
     *            list to store the matching descendants
     * @param crossFindBoundaries
     *            if <code>false</code>, recursion stops for nodes for which
     *            {@link #isFindBoundary()} is <code>true</code>
     * @deprecated Use {@link #findDescendantsOfType(Class, boolean)} instead, which
     * returns a result list.
     */
    @Deprecated
    <T> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossFindBoundaries);

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
    <T> List<T> findDescendantsOfType(Class<T> targetType, boolean crossFindBoundaries);

    /**
     * Traverses the children to find the first instance of type childType.
     *
     * @see #getFirstDescendantOfType(Class) if traversal of the entire tree is
     *      needed.
     *
     * @param childType
     *            class which you want to find.
     * @return Node of type childType. Returns <code>null</code> if none found.
     */
    <T> T getFirstChildOfType(Class<T> childType);

    /**
     * Traverses down the tree to find the first descendant instance of type
     * descendantType without crossing find boundaries.
     *
     * @param descendantType
     *            class which you want to find.
     * @return Node of type descendantType. Returns <code>null</code> if none
     *         found.
     */
    <T> T getFirstDescendantOfType(Class<T> descendantType);

    /**
     * Finds if this node contains a descendant of the given type without crossing find boundaries.
     *
     * @param type
     *            the node type to search
     * @return <code>true</code> if there is at least one descendant of the
     *         given type
     */
    <T> boolean hasDescendantOfType(Class<T> type);

    /**
     * Returns all the nodes matching the xpath expression.
     *
     * @param xpathString
     *            the expression to check
     * @return List of all matching nodes. Returns an empty list if none found.
     * @throws JaxenException if the xpath is incorrect or fails altogether
     *
     * @deprecated This is very inefficient and should not be used in new code. PMD 7.0.0 will remove
     *             support for this method.
     */
    @Deprecated
    List<? extends Node> findChildNodesWithXPath(String xpathString) throws JaxenException;

    /**
     * Checks whether at least one descendant matches the xpath expression.
     *
     * @param xpathString
     *            the expression to check
     * @return true if there is a match
     *
     * @deprecated This is very inefficient and should not be used in new code. PMD 7.0.0 will remove
     *             support for this method.
     */
    @Deprecated
    boolean hasDescendantMatchingXPath(String xpathString);

    /**
     * Get a DOM Document which contains Elements and Attributes representative
     * of this Node and it's children. Essentially a DOM tree representation of
     * the Node AST, thereby allowing tools which can operate upon DOM to also
     * indirectly operate on the AST.
     */
    Document getAsDocument();

    /**
     * Get the user data associated with this node. By default there is no data,
     * unless it has been set via {@link #setUserData(Object)}.
     *
     * @return The user data set on this node.
     */
    Object getUserData();

    /**
     * Set the user data associated with this node.
     *
     * <p>PMD itself will never set user data onto a node. Nor should any Rule
     * implementation, as the AST nodes are shared between concurrently
     * executing Rules (i.e. it is <strong>not</strong> thread-safe).
     *
     * <p>This API is most useful for external applications looking to leverage
     * PMD's robust support for AST structures, in which case application
     * specific annotations on the AST nodes can be quite useful.
     *
     * @param userData
     *            The data to set on this node.
     */
    void setUserData(Object userData);

    /**
     * Remove the current node from its parent.
     *
     * @deprecated This is internal API and will be removed from this interface with 7.0.0
     */
    @Deprecated
    @InternalApi
    void remove();

    /**
     * This method tells the node to remove the child node at the given index from the node's list of
     * children, if any; if not, no changes are done.
     *
     * @param childIndex
     *          The index of the child to be removed
     *
     * @deprecated This is internal API and will be removed from this interface with 7.0.0
     */
    @Deprecated
    @InternalApi
    void removeChildAtIndex(int childIndex);


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
     * Returns an iterator enumerating all the attributes that are available
     * from XPath for this node.
     *
     * @return An attribute iterator for this node
     */
    Iterator<Attribute> getXPathAttributesIterator();

    /**
     * Returns an iterable enumerating the children of this node.
     * Use it with a foreach loop:
     * <pre>{@code
     *      for (Node child : node.children()) {
     *          // process child
     *      }
     * }</pre>
     *
     * <p>This method's return type will be changed to NodeStream
     * in PMD 7, which is a more powerful kind of iterable. The
     * change will be source compatible.
     *
     * @return A new iterable for the children of this node
     */
    Iterable<? extends Node> children();

}
