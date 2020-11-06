/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dcd.graph;

import java.util.Comparator;

import net.sourceforge.pmd.dcd.DCD;

/**
 * Compares MemberNodes. FieldNodes are smaller than ConstructorNodes which are
 * smaller than MethodNodes.
 * @deprecated See {@link DCD}
 */
@Deprecated
public final class MemberNodeComparator implements Comparator<MemberNode> {

    public static final MemberNodeComparator INSTANCE = new MemberNodeComparator();

    private MemberNodeComparator() {
    }

    @Override
    public int compare(MemberNode node1, MemberNode node2) {
        if (node1 instanceof FieldNode) {
            if (node2 instanceof FieldNode) {
                return node1.compareTo(node2);
            } else {
                return -1;
            }
        } else if (node1 instanceof ConstructorNode) {
            if (node2 instanceof FieldNode) {
                return 1;
            } else if (node2 instanceof ConstructorNode) {
                return node1.compareTo(node2);
            } else {
                return -1;
            }
        } else {
            if (node2 instanceof MethodNode) {
                return node1.compareTo(node2);
            } else {
                return 1;
            }
        }
    }
}
