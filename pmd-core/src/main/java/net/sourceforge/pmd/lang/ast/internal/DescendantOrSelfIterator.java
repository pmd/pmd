/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

/** Iterates over a node and its descendants. */
class DescendantOrSelfIterator implements Iterator<@NonNull Node> {

    private final Deque<Node> queue = new ArrayDeque<>();
    private final boolean crossFindBoundaries;

    /** Always {@link #hasNext()} after exiting the constructor. */
    DescendantOrSelfIterator(Node top, boolean crossFindBoundaries) {
        this.crossFindBoundaries = crossFindBoundaries;
        queue.addFirst(top);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }


    @Override
    public @NonNull Node next() {
        Node node = queue.removeFirst();
        enqueueChildren(node);
        return node;
    }


    private void enqueueChildren(Node n) {
        if (crossFindBoundaries || !n.isFindBoundary()) {
            for (int i = n.jjtGetNumChildren() - 1; i >= 0; i--) {
                queue.addFirst(n.jjtGetChild(i));
            }
        }
    }
}
