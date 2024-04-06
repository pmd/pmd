package net.sourceforge.pmd.lang.document;

import java.util.Optional;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Utilities to find a node at specific text coordinates.
 */
@Experimental
public class NodeFindingUtil {


    /**
     * Locates the innermost node in the subtree rooted in the given node
     * that contains the given offset.
     */
    public static Optional<Node> findNodeAt(Node root, int offset) {
        return Optional.ofNullable(findNodeRec(root, offset))
                       .filter(it -> it.getTextRegion().contains(offset));
    }


    /**
     * Simple recursive search algo. Assumes that the regions of siblings
     * do not overlap and that parents contain regions of children entirely.
     * - We only have to explore one node at each level of the tree, and we quickly
     * hit the bottom (average depth of a Java AST ~20-25, with 6.x.x grammar).
     * - At each level, the next node to explore is chosen via binary search.
     */
    private static Node findNodeRec(Node subject, int offset) {
        Node child = binarySearchInChildren(subject, offset);
        return child == null ? subject : findNodeRec(child, offset);
    }

    // returns the child of the [parent] that contains the target
    // it's assumed to be unique
    private static Node binarySearchInChildren(Node parent, int offset) {

        int low = 0;
        int high = parent.getNumChildren() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Node child = parent.getChild(mid);
            TextRegion childRegion = child.getTextRegion();
            int cmp = Integer.compare(childRegion.getStartOffset(), offset);

            if (cmp < 0) {
                // node start is before target
                low = mid + 1;
                if (childRegion.getEndOffset() >= offset) {
                    // node end is after target
                    return child;
                }
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                // target is node start position
                return child; // key found
            }
        }
        return null;  // key not found
    }

    /**
     * Returns the innermost node that covers the entire given text range
     * in the given tree.
     *
     * @param root  Root of the tree
     * @param range Range to find
     * @param exact If true, will return the *outermost* node whose range
     *              is *exactly* the given text range, otherwise it may be larger.
     */
    public static Optional<Node> findNodeCovering(Node root, TextRegion range, boolean exact) {
        return findNodeAt(root, range.getStartOffset()).map(innermost -> {
            for (Node parent : innermost.ancestorsOrSelf()) {
                TextRegion parentRange = parent.getTextRegion();
                if (!exact && parentRange.contains(range)) {
                    return parent;
                } else if (exact && parentRange.equals(range)) {
                    return parent;
                } else if (exact && parentRange.contains(range)) {
                    // if it isn't the same, then we can't find better so better stop looking
                    return null;
                }
            }
            return null;
        });
    }


}
