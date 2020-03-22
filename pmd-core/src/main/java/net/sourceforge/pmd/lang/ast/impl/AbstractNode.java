/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

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
public abstract class AbstractNode<T extends Node> implements Node {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    @Deprecated
    public static final SimpleDataKey<Object> LEGACY_USER_DATA = DataMap.simpleDataKey("legacy user data");

    // lazy initialized, many nodes don't need it
    private @Nullable DataMap<DataKey<?, ?>> userData;

    // never null, never contains null elements
    protected Node[] children = EMPTY_ARRAY;
    private T parent;
    private int childIndex;

    private DataFlowNode dataFlowNode;
    // @Deprecated?
    private String image;

    public AbstractNode() {

    }


    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public int getIndexInParent() {
        return childIndex;
    }

    @Override
    public T getChild(final int index) {
        return (T) children[index];
    }

    @Override
    public int getNumChildren() {
        return children.length;
    }

    protected void setParent(final T parent) {
        this.parent = parent;
    }


    /**
     * This method tells the node to add its argument to the node's list of children.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     */
    protected void addChild(final T child, final int index) {
        if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        AbstractNode<T> child1 = (AbstractNode<T>) child;
        child1.setChildIndex(index);
        child1.setParent((T) this);
    }

    protected void remove() {
        // Detach current node of its parent, if any
        final AbstractNode<T> parent = (AbstractNode<T>) getParent();
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



    @Override
    @SuppressWarnings("unchecked")
    public NodeStream<? extends T> children() {
        return (NodeStream<T>) Node.super.children();
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

    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     */
    public final boolean hasDescendantOfAnyType(final Class<? extends Node>... types) {
        // TODO consider implementing that with a single traversal!
        // -> this is done if you use node streams
        for (final Class<? extends Node> type : types) {
            if (hasDescendantOfType(type)) {
                return true;
            }
        }
        return false;
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
