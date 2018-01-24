/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * <p>
 * Describe a modification over a {@link Node} so as to be able to track changes over it.
 * </p>
 * <p>
 * The modification is described by specifying the node on which the modification has occurred ({@code parentNode}),
 * the old child node ({@code oldChildNode}), the new child node ({@code newChildNode}) and the index of the
 * child node being modified ({@code childNodeIndex}).
 * </p>
 * <p>
 * The {@code rewriteEventType} is obtained based on the provided values for
 * {@code oldChildNode} and {@code newChildNode} (which may be null).
 * </p>
 */
public class RewriteEvent {
    private final Node parentNode;
    private final Node oldChildNode;
    private final Node newChildNode;
    private final int childNodeIndex;
    private final RewriteEventType rewriteEventType;

    public RewriteEvent(final Node parentNode,
                        final Node oldChildNode,
                        final Node newChildNode,
                        final int childNodeIndex) {
        this.parentNode = Objects.requireNonNull(parentNode);
        this.oldChildNode = oldChildNode;
        this.newChildNode = newChildNode;
        this.childNodeIndex = requireNonNegative(childNodeIndex);
        this.rewriteEventType = grabRewriteEventType();
    }

    private RewriteEventType grabRewriteEventType() {
        if (Objects.equals(oldChildNode, newChildNode)) {
            throw new IllegalArgumentException("Cannot generate a rewrite event with both child nodes being equal");
        } else if (oldChildNode == null) { // newChildNode not null as they are not equal
            return RewriteEventType.INSERT;
        } else if (newChildNode == null) { // oldChildNode not null as they are not equal
            return RewriteEventType.REMOVE;
        } else {  // oldChildNode & newChildNode are both not null & not equal
            return RewriteEventType.REPLACE;
        }
    }

    private int requireNonNegative(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format("n <%d> is lower than 0", n));
        }
        return n;
    }

    public RewriteEventType getRewriteEventType() {
        return rewriteEventType;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public Node getOldChildNode() {
        return oldChildNode;
    }

    public Node getNewChildNode() {
        return newChildNode;
    }

    public int getChildNodeIndex() {
        return childNodeIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RewriteEvent rewriteEvent = (RewriteEvent) o;
        return childNodeIndex == rewriteEvent.childNodeIndex
            && rewriteEventType == rewriteEvent.rewriteEventType
            && Objects.equals(parentNode, rewriteEvent.parentNode)
            && Objects.equals(oldChildNode, rewriteEvent.oldChildNode)
            && Objects.equals(newChildNode, rewriteEvent.newChildNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rewriteEventType, parentNode, oldChildNode, newChildNode, childNodeIndex);
    }
}
