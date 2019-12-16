/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

/**
 * Base class for implementations of the {@link Node} interface
 * that use JJTree.
 */
public abstract class AbstractJjtreeNode implements Node {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    protected Node parent;
    // never null, never contains null elements
    protected Node[] children = EMPTY_ARRAY;
    protected int childIndex;

    private String image;
    private DataFlowNode dataFlowNode;
    private Object userData;
    protected JavaccToken firstToken;
    protected JavaccToken lastToken;

    protected AbstractJjtreeNode() {

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
    public void jjtSetParent(final Node parent) {
        this.parent = parent;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(final Node child, final int index) {
        if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.jjtSetChildIndex(index);
        child.jjtSetParent(this);
    }

    @Override
    public void jjtSetChildIndex(final int index) {
        childIndex = index;
    }

    @Override
    public int jjtGetChildIndex() {
        return childIndex;
    }

    @Override
    public Node jjtGetChild(final int index) {
        return children[index];
    }

    @Override
    public int jjtGetNumChildren() {
        return children.length;
    }

    @Override
    public int jjtGetId() {
        return 0;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public void setImage(final String image) {
        this.image = image;
    }

    @Override
    public boolean hasImageEqualTo(final String image) {
        return Objects.equals(this.getImage(), image);
    }

    @Override
    public int getBeginLine() {
        return firstToken.getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return firstToken.getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return lastToken.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return lastToken.getEndColumn();
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
        // hasDescendantOfType could then be a special case of this one
        // But to really share implementations, getFirstDescendantOfType's
        // internal helper could have to give up some type safety to rely
        // instead on a getFirstDescendantOfAnyType, then cast to the correct type
        for (final Class<? extends Node> type : types) {
            if (hasDescendantOfType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    public final JavaccToken getFirstToken() {
        return firstToken;
    }

    public final JavaccToken getLastToken() {
        return lastToken;
    }

    protected void setFirstToken(final JavaccToken token) {
        this.firstToken = token;
    }

    protected void setLastToken(final JavaccToken token) {
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

    @Override
    public String toString() {
        return getXPathNodeName();
    }

}
