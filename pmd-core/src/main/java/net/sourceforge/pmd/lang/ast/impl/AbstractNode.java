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
 * @param <T> Public interface for nodes of this language (eg JavaNode
 *            in the java module).
 */
public abstract class AbstractNode<T extends GenericNode<T>> implements GenericNode<T> {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    // lazy initialized, many nodes don't need it
    private @Nullable DataMap<DataKey<?, ?>> userData;

    // never null, never contains null elements
    private Node[] children = EMPTY_ARRAY;
    private AbstractNode<T> parent;
    private int childIndex;

    protected AbstractNode() {
        // only for subclassing
    }

    @Override
    public T getParent() {
        return toPublic(parent);
    }

    @Override
    public int getIndexInParent() {
        return childIndex;
    }

    @Override
    public T getChild(final int index) {
        return toPublic(children[index]);
    }

    @Override
    public int getNumChildren() {
        return children.length;
    }

    protected void setParent(final AbstractNode<T> parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    protected T toPublic(Node n) {
        return (T) n;
    }

    /**
     * This method tells the node to add its argument to the node's list of children.
     * Note that it is more efficient to add children in reverse (from right to left),
     * because the array is resized only once.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     */
    protected void addChild(final AbstractNode<T> child, final int index) {
        if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.setChildIndex(index);
        child.setParent(this);
    }



    @SafeVarargs
    protected final void setChildren(AbstractNode<T>... newChildren) {
        this.children = new Node[newChildren.length];
        System.arraycopy(newChildren, 0, this.children, 0, newChildren.length);
        for (int i = 0; i < newChildren.length; i++) {
            newChildren[i].setParent(this);
            newChildren[i].setChildIndex(i);
        }
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
                ((AbstractNode<T>) getChild(i)).setChildIndex(i);
            }
        }
    }

    /**
     * Sets the index of this node from the perspective of its parent. This
     * means: this.getParent().getChild(index) == this.
     *
     * @param index the child index
     */
    protected void setChildIndex(final int index) {
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

}
