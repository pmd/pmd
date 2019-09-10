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

    public static <T extends Node> void findDescendantsOfType(final Node node, final Class<T> targetType, final List<T> results,
                                                              final boolean crossFindBoundaries) {

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            final Node child = node.jjtGetChild(i);
            if (targetType.isAssignableFrom(child.getClass())) {
                results.add(targetType.cast(child));
            }

            if (crossFindBoundaries || !child.isFindBoundary()) {
                findDescendantsOfType(child, targetType, results, crossFindBoundaries);
            }
        }
    }

    static <T extends Node> T getFirstDescendantOfType(final Node node, final Class<T> descendantType) {
        final int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            final Node n1 = node.jjtGetChild(i);
            if (descendantType.isAssignableFrom(n1.getClass())) {
                return descendantType.cast(n1);
            }
            if (!n1.isFindBoundary()) {
                final T n2 = getFirstDescendantOfType(n1, descendantType);
                if (n2 != null) {
                    return n2;
                }
            }
        }
        return null;
    }

    static <T extends Node> T getFirstParentOfType(final Node node, final Class<T> type) {
        Node n = node.jjtGetParent();
        while (n != null) {
            if (type.isInstance(n)) {
                return type.cast(n);
            }
            n = n.jjtGetParent();
        }
        return null;
    }

    static <T extends Node> T getFirstChildOfType(final Node node, final Class<T> type) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }
        return null;
    }

    static <T extends Node> T getLastChildOfType(final Node node, final Class<T> type) {
        for (int i = node.jjtGetNumChildren() - 1; i >= 0; i--) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }
        return null;
    }

    static <T extends Node> List<T> findChildrenOfType(final Node node, final Class<T> type) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                list.add(type.cast(c));
            }
        }
        return list;
    }

    static <T extends Node> int countChildrenOfType(final Node node, final Class<T> type) {
        if (type == Node.class) {
            return node.jjtGetNumChildren();
        }
        int sum = 0;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                sum++;
            }
        }
        return sum;
    }


    static Iterator<Node> childrenIterator(Node parent) {
        return childrenIterator(parent, 0, parent.jjtGetNumChildren());
    }

    static Iterator<Node> childrenIterator(Node parent, int from, int to) {
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
