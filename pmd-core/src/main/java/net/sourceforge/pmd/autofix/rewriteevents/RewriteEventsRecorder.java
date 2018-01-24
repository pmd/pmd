/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * All classes implementing this interface should
 * record all modifications that occur over the children of parent node.
 */
public interface RewriteEventsRecorder {

    /**
     * Record a remove operation over the given {@code parentNode}.
     *
     * @param parentNode   The node whose child is being removed.
     * @param oldChildNode The child node being removed.
     * @param childIndex   The index of the child node being removed.
     */
    void recordRemove(Node parentNode, Node oldChildNode, int childIndex);

    /**
     * Record an insert operation over the given {@code parentNode}.
     *
     * @param parentNode   The node on which a new child is being inserted.
     * @param newChildNode The child node being inserted.
     * @param childIndex   The index where the new child node is being inserted.
     */
    void recordInsert(Node parentNode, Node newChildNode, int childIndex);

    /**
     * Record a replace operation over the given {@code parentNode}.
     *
     * @param parentNode   The node whose child is being replaced.
     * @param oldChildNode The child node being replaced.
     * @param newChildNode The new child node that will replace the {@code oldChildNode}.
     * @param childIndex   The index of the child node being replaced.
     */
    void recordReplace(Node parentNode, Node oldChildNode, Node newChildNode, int childIndex);

    /**
     * @return {@code true} if this instance holds any rewrite event; {@code false} otherwise.
     */
    boolean hasRewriteEvents();

    /**
     * @return A copy of all the {@link RewriteEvent}s held by this instance (may be null).
     */
    RewriteEvent[] getRewriteEvents();
}
