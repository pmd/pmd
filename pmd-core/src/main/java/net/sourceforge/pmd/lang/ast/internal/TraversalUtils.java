/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

final class TraversalUtils {

    private TraversalUtils() {

    }

    static <T extends Node> List<T> findDescendantsMatching(final Node node,
                                                            final Filtermap<Node, T> filtermap,
                                                            final TraversalConfig config) {
        List<T> results = new ArrayList<>();
        findDescendantsMatching(node, filtermap, results, config);
        return results;
    }

    static <T extends Node> void findDescendantsMatching(final Node node,
                                                         final Filtermap<Node, T> filtermap,
                                                         final List<T> results,
                                                         final TraversalConfig config) {

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            final Node child = node.jjtGetChild(i);
            final T mapped = filtermap.apply(child);
            if (mapped != null) {
                results.add(mapped);
            }

            if (config.isCrossFindBoundaries() || !child.isFindBoundary()) {
                findDescendantsMatching(child, filtermap, results, config);
            }
        }
    }

    static <T extends Node> T getFirstDescendantOfType(final Node node, final Filtermap<Node, T> filtermap, TraversalConfig config) {
        final int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            Node child = node.jjtGetChild(i);
            final T t = filtermap.apply(child);
            if (t != null) {
                return t;
            } else if (config.isCrossFindBoundaries() || !child.isFindBoundary()) {
                final T n2 = getFirstDescendantOfType(child, filtermap, config);
                if (n2 != null) {
                    return n2;
                }
            }
        }
        return null;
    }

    static <T extends Node> T getFirstParentOrSelfMatching(final Node node, final Filtermap<Node, T> filter) {
        Node n = node;
        while (n != null) {
            T t = filter.apply(n);
            if (t != null) {
                return t;
            }
            n = n.jjtGetParent();
        }
        return null;
    }

    static <T extends Node> T getFirstChildMatching(final Node node, final Filtermap<Node, T> filter, int from, int len) {
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T extends Node> T getLastChildMatching(final Node node, final Filtermap<Node, T> filter, int from, int len) {
        for (int i = from + len - 1; i >= from; i--) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T extends Node> List<T> findChildrenMatching(final Node node, final Filtermap<Node, T> filter, int from, int len) {
        List<T> list = new ArrayList<>();
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    static <T extends Node> int countChildrenMatching(final Node node, final Filtermap<Node, T> filter, int from, int len) {
        if (filter == Filtermap.NODE_IDENTITY && from == 0 && len == node.jjtGetNumChildren()) {
            return node.jjtGetNumChildren();
        }
        int sum = 0;
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.jjtGetChild(i);
            T t = filter.apply(c);
            if (t != null) {
                sum++;
            }
        }
        return sum;
    }


    static Iterator<Node> childrenIterator(Node parent) {
        assert parent != null : "parent should not be null";
        return childrenIterator(parent, 0, parent.jjtGetNumChildren());
    }

    static Iterator<Node> childrenIterator(Node parent, final int from, final int to) {
        assert parent != null : "parent should not be null";
        assert from >= 0 && from <= parent.jjtGetNumChildren() : "'from' should be a valid index";
        assert to >= 0 && to <= parent.jjtGetNumChildren() : "'to' should be a valid index";
        assert from <= to : "'from' should be lower than 'to'";

        if (parent.jjtGetNumChildren() == 0 || to == from) {
            return Collections.emptyIterator();
        }

        return new Iterator<Node>() {

            private int i = from;

            @Override
            public boolean hasNext() {
                return i < to;
            }

            @Override
            public @NonNull
            Node next() {
                return parent.jjtGetChild(i++);
            }
        };
    }

}
