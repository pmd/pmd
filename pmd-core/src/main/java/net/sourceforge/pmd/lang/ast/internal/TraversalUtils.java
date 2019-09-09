/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.List;

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

    public static <T extends Node> T getFirstDescendantOfType(final Class<T> descendantType, final Node node) {
        final int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            final Node n1 = node.jjtGetChild(i);
            if (descendantType.isAssignableFrom(n1.getClass())) {
                return descendantType.cast(n1);
            }
            if (!n1.isFindBoundary()) {
                final T n2 = getFirstDescendantOfType(descendantType, n1);
                if (n2 != null) {
                    return n2;
                }
            }
        }
        return null;
    }

    public static <T extends Node> T getFirstParentOfType(final Class<T> type, final Node node) {
        Node n = node.jjtGetParent();
        while (n != null) {
            if (type.isInstance(n)) {
                return type.cast(n);
            }
            n = n.jjtGetParent();
        }
        return null;
    }

    public static <T extends Node> T getFirstChildOfType(final Class<T> type, final Node node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }
        return null;
    }

    static <T extends Node> List<T> findChildrenOfType(final Class<T> type, final Node node) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node c = node.jjtGetChild(i);
            if (type.isInstance(c)) {
                list.add(type.cast(c));
            }
        }
        return list;
    }

    static <T extends Node> int countChildrenOfType(final Class<T> type, final Node node) {
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
}
