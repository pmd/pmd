/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

public final class TraversalUtils {

    private TraversalUtils() {

    }

    public static <T extends Node> void findDescendantsOfType(final Node node, Class<T> type, final List<T> results,
                                                              final boolean crossFindBoundaries) {
        findDescendantsOfType(node, Filtermap.isInstance(type), results, crossFindBoundaries);
    }

    static <T extends Node> void findDescendantsOfType(final Node node, final Filtermap<Node, T> filtermap, final List<T> results,
                                                       final boolean crossFindBoundaries) {

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            final Node child = node.jjtGetChild(i);
            final T mapped = filtermap.apply(child);
            if (mapped != null) {
                results.add(mapped);
            }

            if (crossFindBoundaries || !child.isFindBoundary()) {
                findDescendantsOfType(child, filtermap, results, crossFindBoundaries);
            }
        }
    }

    static <T extends Node> T getFirstDescendantOfType(final Node node, final Filtermap<Node, T> filtermap) {
        final int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            Node child = node.jjtGetChild(i);
            final T t = filtermap.apply(child);
            if (t != null) {
                return t;
            } else if (!child.isFindBoundary()) {
                final T n2 = getFirstDescendantOfType(child, filtermap);
                if (n2 != null) {
                    return n2;
                }
            }
        }
        return null;
    }

    static <T extends Node> T getFirstParentOrSelfMatching(final Node node, final Filtermap<Node, T> filtermap) {
        Node n = node;
        while (n != null) {
            T t = filtermap.apply(n);
            if (t != null) {
                return t;
            }
            n = n.jjtGetParent();
        }
        return null;
    }

    static <T extends Node> T getFirstChildMatching(final Node node, final Filtermap<Node, T> filter) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T extends Node> T getLastChildMatching(final Node node, final Filtermap<Node, T> filter) {
        for (int i = node.jjtGetNumChildren() - 1; i >= 0; i--) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T extends Node> List<T> findChildrenMatching(final Node node, final Filtermap<Node, T> filter) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    static <T extends Node> int countChildrenMatching(final Node node, final Filtermap<Node, T> filter) {
        if (filter == Filtermap.NODE_IDENTITY) {
            return node.jjtGetNumChildren();
        }
        int sum = 0;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                sum++;
            }
        }
        return sum;
    }


    static Iterator<Node> childrenIterator(Node parent) {
        return childrenIterator(parent, 0, parent.jjtGetNumChildren());
    }

    static Iterator<Node> childrenIterator(Node parent, final int from, final int to) {
        return new Iterator<Node>() {

            private int i = Math.max(from, 0);

            @Override
            public boolean hasNext() {
                return i < to && i < parent.jjtGetNumChildren();
            }

            @Override
            public @NonNull
            Node next() {
                return parent.jjtGetChild(i++);
            }
        };
    }

}
