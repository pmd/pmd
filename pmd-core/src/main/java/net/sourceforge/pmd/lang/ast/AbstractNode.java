/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.annotation.InternalApi;
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
public abstract class AbstractNode implements Node {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    @Deprecated
    public static final SimpleDataKey<Object> LEGACY_USER_DATA = DataMap.simpleDataKey("legacy user data");

    private final DataMap<DataKey<?, ?>> userData = DataMap.newDataMap();

    /**
     * @deprecated Use {@link #getParent()}
     */
    @Deprecated
    protected Node parent;
    // never null, never contains null elements
    protected Node[] children = EMPTY_ARRAY;
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
        return childIndex;
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
    @Deprecated
    @InternalApi
    public void jjtSetChildIndex(final int index) {
        childIndex = index;
    }


    @Override
    @Deprecated
    public Node jjtGetChild(final int index) {
        return children[index];
    }

    @Override
    @Deprecated
    public int jjtGetNumChildren() {
        return children.length;
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
            if (children.length > 0) {
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
    @Deprecated
    public Object getUserData() {
        return userData.get(LEGACY_USER_DATA);
    }

    @Override
    @Deprecated
    public void setUserData(final Object userData) {
        this.userData.set(LEGACY_USER_DATA, userData);
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        return userData;
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
        this.beginLine = token.getBeginLine();
        this.beginColumn = token.getBeginColumn();
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
        this.endLine = token.getEndLine();
        this.endColumn = token.getEndColumn();
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

    @Override
    public String toString() {
        return getXPathNodeName();
    }

}
