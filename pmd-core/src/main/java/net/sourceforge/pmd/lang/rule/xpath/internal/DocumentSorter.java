/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Comparator;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Sorts nodes by document order.
 */
final class DocumentSorter implements Comparator<Node> {

    public static final DocumentSorter INSTANCE = new DocumentSorter();

    private DocumentSorter() {

    }

    @Override
    public int compare(Node node1, Node node2) {
        if (node1 == null && node2 == null) {
            return 0;
        } else if (node1 == null) {
            return -1;
        } else if (node2 == null) {
            return 1;
        }
        int result = node1.getBeginLine() - node2.getBeginLine();
        if (result == 0) {
            result = node1.getBeginColumn() - node2.getBeginColumn();
        }
        return result;
    }
}
