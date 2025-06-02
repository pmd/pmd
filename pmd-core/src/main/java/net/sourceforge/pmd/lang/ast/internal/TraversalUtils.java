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

    /*
        Note that the methods of this class must not use node streams
        to iterate on children, because node streams are implemented
        using these methods.
     */

    private TraversalUtils() {

    }

    static <T extends Node> T getFirstParentOrSelfMatching(final Node node, final Filtermap<? super Node, ? extends T> filter) {
        Node n = node;
        while (n != null) {
            T t = filter.apply(n);
            if (t != null) {
                return t;
            }
            n = n.getParent();
        }
        return null;
    }

    static <T extends Node> T getFirstChildMatching(final Node node, final Filtermap<? super Node, ? extends T> filter, int from, int len) {
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.getChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T extends Node> T getLastChildMatching(final Node node, final Filtermap<? super Node, ? extends T> filter, int from, int len) {
        for (int i = from + len - 1; i >= from; i--) {
            Node c = node.getChild(i);
            T t = filter.apply(c);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    static <T> List<T> findChildrenMatching(final Node node, final Filtermap<? super Node, ? extends T> filter, int from, int len) {
        return findChildrenMatching(node, filter, from, len, Integer.MAX_VALUE);
    }

    static <T> List<T> findChildrenMatching(final Node node, final Filtermap<? super Node, ? extends T> filter, int from, int len, int maxSize) {
        if (maxSize == 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.getChild(i);
            T t = filter.apply(c);
            if (t != null) {
                list.add(t);
                if (list.size() >= maxSize) {
                    return list;
                }
            }
        }
        return list;
    }

    static <T extends Node> int countChildrenMatching(final Node node, final Filtermap<Node, T> filter, int from, int len) {
        int sum = 0;
        for (int i = from, last = from + len; i < last; i++) {
            Node c = node.getChild(i);
            T t = filter.apply(c);
            if (t != null) {
                sum++;
            }
        }
        return sum;
    }


    static Iterator<Node> childrenIterator(Node parent, final int from, final int to) {
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
            public @NonNull
            Node next() {
                return parent.getChild(i++);
            }
        };
    }

}
