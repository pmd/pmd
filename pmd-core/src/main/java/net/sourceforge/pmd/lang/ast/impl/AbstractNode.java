/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Base class for implementations of the Node interface whose children
 * are stored in an array. This class provides the basic utilities to
 * link children and parent. It's used by most most nodes, but currently
 * not the antlr nodes, so downcasting {@link Node} to this class may fail
 * and is very bad practice.
 *
 * @param <B> Self type (eg AbstractJavaNode in the java module), this
 *            must ultimately implement {@code <N>}, though the java type
 *            system does not allow us to express that
 * @param <N> Public interface for nodes of this language (eg JavaNode
 *            in the java module).
 */
public abstract class AbstractNode<B extends AbstractNode<B, N>,
    // node the Node as first bound here is to make casts from Node to N noops at runtime.
    N extends Node & GenericNode<N>> implements GenericNode<N> {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    // lazy initialized, many nodes don't need it
    private @Nullable DataMap<DataKey<?, ?>> userData;

    // never null, never contains null elements
    private Node[] children = EMPTY_ARRAY;
    private B parent;
    private int childIndex;

    protected AbstractNode() {
        // only for subclassing
    }

    @Override
    public final N getParent() {
        return (N) parent;
    }

    @Override
    public final int getIndexInParent() {
        return childIndex;
    }

    @Override
    public final N getChild(final int index) {
        return (N) children[index];
    }

    @Override
    public final int getNumChildren() {
        return children.length;
    }

    protected void setParent(final B parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    private B asSelf(Node n) {
        return (B) n;
    }

    /**
     * Set the child at the given index to the given node. This resizes
     * the children array to be able to contain the given index. Implementations
     * must take care that this does not leave any "holes" in the array.
     * This method throws if there is already a child at the given index.
     *
     * <p>Note that it is more efficient to add children in reverse
     * (from right to left), because the array is resized only the
     * first time.
     *
     * <p>This method also calls {@link #setParent(AbstractNode)}.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     */
    protected void addChild(final B child, final int index) {
        assert index >= 0 : "Invalid index " + index;
        assert index >= children.length || children[index] == null : "There is already a child at index " + index;

        if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }

        setChild(child, index);
    }

    /**
     * Set the child at the given index. The difference with {@link #addChild(AbstractNode, int) addChild}
     * is that the index must exist, while addChild may resizes the array.
     */
    protected void setChild(final B child, final int index) {
        assert index >= 0 && index < children.length : "Invalid index " + index + " for length " + children.length;
        children[index] = child;
        child.setChildIndex(index);
        child.setParent(asSelf(this));
    }

    /**
     * Insert a child at the given index, shifting all the following
     * children to the right.
     *
     * @param child New child
     * @param index Index (must be 0 <= index <= getNumChildren()), ie
     *              you can insert a node beyond the end, because that
     *              would leave holes in the array
     */
    protected void insertChild(final B child, final int index) {
        assert index >= 0 && index <= children.length
            : "Invalid index for insertion into array of length " + children.length + ": " + index;

        Node[] newChildren = new Node[children.length + 1];
        if (index != 0) {
            System.arraycopy(children, 0, newChildren, 0, index);
        }
        if (index != children.length) {
            System.arraycopy(children, index, newChildren, index + 1, children.length - index);
        }
        newChildren[index] = child;
        child.setParent(asSelf(this));

        for (int i = index; i < newChildren.length; i++) {
            asSelf(newChildren[i]).setChildIndex(i);
        }
        this.children = newChildren;
    }


    protected void remove() {
        // Detach current node of its parent, if any
        if (parent != null) {
            parent.removeChildAtIndex(getIndexInParent());
            setParent(null);
        }

        // TODO [autofix]: Notify action for handling text edition
    }

    protected void removeChildAtIndex(final int childIndex) {
        if (0 <= childIndex && childIndex < getNumChildren()) {
            // Remove the child at the given index
            children = ArrayUtils.remove(children, childIndex);
            // Update the remaining & left-shifted children indexes
            for (int i = childIndex; i < getNumChildren(); i++) {
                asSelf(getChild(i)).setChildIndex(i);
            }
        }
    }

    /**
     * Sets the index of this node from the perspective of its parent. This
     * means: this.getParent().getChild(index) == this.
     *
     * @param index the child index
     */
    void setChildIndex(final int index) {
        childIndex = index;
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        if (userData == null) {
            userData = DataMap.newDataMap();
        }
        return userData;
    }


    @Override
    public String toString() {
        return getXPathNodeName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <R extends Node> @Nullable R firstChild(Class<? extends R> rClass) {
        // This operation is extremely common so we give it an optimal
        // implementation, based directly on the array. This will never
        // create a node stream object, and array bounds are not checked.
        // It's final so it can be inlined.
        for (Node child : children) {
            if (rClass.isInstance(child)) {
                // rClass.cast(child) is more expensive than this
                // unchecked cast, which we know is safe.
                return (R) child;
            }
        }
        return null;
    }
}
