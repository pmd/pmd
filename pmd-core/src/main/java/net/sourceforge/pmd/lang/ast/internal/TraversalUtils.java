/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Collections;
import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Internal tree traversal methods.
 */
public final class TraversalUtils {

    /**
     * Returns an iterator over the children of a Node.
     */
    public static Iterator<Node> childrenIterator(Node parent) {
        assert parent != null : "parent should not be null";
        return childrenIterator(parent, 0, parent.jjtGetNumChildren());
    }

    static Iterator<Node> childrenIterator(final Node parent, final int from, final int to) {
        assert parent != null : "parent should not be null";
        assert from >= 0 && from <= parent.getNumChildren() : "'from' should be a valid index";
        assert to >= 0 && to <= parent.getNumChildren() : "'to' should be a valid index";
        assert from <= to : "'from' should be lower than 'to'";

        if (to == from) {
            return Collections.emptyIterator();
        }

        return new Iterator<Node>() {

            private int i = from;

            @Override
            public boolean hasNext() {
                return i < to;
            }

            @Override
            public Node next() {
                return parent.getChild(i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove");
            }
        };
    }
}
