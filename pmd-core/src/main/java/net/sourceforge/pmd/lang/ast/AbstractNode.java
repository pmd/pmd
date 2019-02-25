/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;

/**
 * Base class for all implementations of the Node interface.
 */
public abstract class AbstractNode implements Node {

    private static final Logger LOG = Logger.getLogger(AbstractNode.class.getName());

    protected Node parent;
    protected Node[] children;
    protected int childIndex;
    protected int id;

    private String image;
    protected int beginLine = -1;
    protected int endLine;
    protected int beginColumn = -1;
    protected int endColumn;
    private DataFlowNode dataFlowNode;
    private Object userData;
    protected GenericToken firstToken;
    protected GenericToken lastToken;

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

    public boolean isSingleLine() {
        return beginLine == endLine;
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
        if (children == null) {
            children = new Node[index + 1];
        } else if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.jjtSetChildIndex(index);
    }

    @Override
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
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
        return children == null ? 0 : children.length;
    }

    @Override
    public int jjtGetId() {
        return id;
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
        return beginLine;
    }

    public void testingOnlySetBeginLine(int i) {
        this.beginLine = i;
    }

    @Override
    public int getBeginColumn() {
        if (beginColumn == -1) {
            if (children != null && children.length > 0) {
                return children[0].getBeginColumn();
            } else {
                throw new RuntimeException("Unable to determine beginning line of Node.");
            }
        } else {
            return beginColumn;
        }
    }

    public void testingOnlySetBeginColumn(final int i) {
        this.beginColumn = i;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    public void testingOnlySetEndLine(final int i) {
        this.endLine = i;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

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

    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     */
    public final boolean hasDescendantOfAnyType(final Class<?>... types) {
        // TODO consider implementing that with a single traversal!
        // hasDescendantOfType could then be a special case of this one
        // But to really share implementations, getFirstDescendantOfType's
        // internal helper could have to give up some type safety to rely
        // instead on a getFirstDescendantOfAnyType, then cast to the correct type
        for (final Class<?> type : types) {
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

    public GenericToken jjtGetFirstToken() {
        return firstToken;
    }

    public void jjtSetFirstToken(final GenericToken token) {
        this.firstToken = token;
    }

    public GenericToken jjtGetLastToken() {
        return lastToken;
    }

    public void jjtSetLastToken(final GenericToken token) {
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>This default implementation adds compatibility with the previous
     * way to get the xpath node name, which used {@link Object#toString()}.
     * <p>
     * <p>Please override it. It may be removed in a future major version.
     */
    @Override
    // @Deprecated // FUTURE 7.0.0 make abstract
    public String getXPathNodeName() {
        LOG.warning("getXPathNodeName should be overriden in classes derived from AbstractNode. "
            + "The implementation is provided for compatibility with existing implementors,"
            + "but could be declared abstract as soon as release " + PMDVersion.getNextMajorRelease()
            + ".");
        return toString();
    }
}
