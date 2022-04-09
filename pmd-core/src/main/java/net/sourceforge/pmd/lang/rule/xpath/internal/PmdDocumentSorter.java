/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Comparator;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Sorts nodes by document order.
 */
// renamed because it conflicts with a Saxon node
final class PmdDocumentSorter implements Comparator<Node> {

    public static final PmdDocumentSorter INSTANCE = new PmdDocumentSorter();

    private PmdDocumentSorter() {

    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public int compare(Node node1, Node node2) {
        if (node1 == node2) {
            return 0;
        } else if (node1 == null) {
            return -1;
        } else if (node2 == null) {
            return 1;
        }

        return node1.compareLocation(node2);
    }
}
