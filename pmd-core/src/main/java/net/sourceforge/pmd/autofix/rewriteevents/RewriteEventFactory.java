/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Factory of {@link RewriteEvent} objects.
 */
public abstract class RewriteEventFactory {
    /**
     * @param parentNode   The parent node over which the modification is occurring.
     * @param newChildNode The new child node value being inserted.
     * @param childIndex   The index of the child node being modified.
     * @return A new {@link RewriteEvent} of type {@code INSERT} containing the given values.
     */
    public static RewriteEvent newInsertRewriteEvent(final Node parentNode, final Node newChildNode, final int childIndex) {
        return new RewriteEvent(parentNode, null, newChildNode, childIndex);
    }

    /**
     * @param parentNode   The parent node over which the modification is occurring.
     * @param oldChildNode The old child node value being removed.
     * @param childIndex   The index of the child node being modified.
     * @return A new {@link RewriteEvent} of type {@code REMOVE} containing the given values.
     */
    public static RewriteEvent newRemoveRewriteEvent(final Node parentNode, final Node oldChildNode, final int childIndex) {
        return new RewriteEvent(parentNode, oldChildNode, null, childIndex);
    }

    /**
     * @param parentNode   The parent node over which the modification is occurring.
     * @param oldChildNode The new child node value being removed.
     * @param newChildNode The new child node value being inserted.
     * @param childIndex   The index of the child node being modified.
     * @return A new {@link RewriteEvent} of type {@code REPLACE} containing the given values.
     */
    public static RewriteEvent newReplaceRewriteEvent(final Node parentNode, final Node oldChildNode, final Node newChildNode, final int childIndex) {
        return new RewriteEvent(parentNode, oldChildNode, newChildNode, childIndex);
    }
}
