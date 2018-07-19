/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import static net.sourceforge.pmd.util.fxdesigner.util.IteratorUtil.parentIterator;
import static net.sourceforge.pmd.util.fxdesigner.util.IteratorUtil.reverse;

import java.util.Iterator;
import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.TreeItem;

/**
 * Represents a tree item (data, not UI) in the ast TreeView.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class ASTTreeItem extends TreeItem<Node> {


    private ASTTreeItem(Node n) {
        super(n);
        setExpanded(true);
    }


    /**
     * Finds the tree item corresponding to the given node
     * among the descendants of this item. This method assumes
     * this item is the root node.
     *
     * @param node The node to find
     *
     * @return The found item, or null if this item doesn't wrap the
     *         root of the tree to which the parameter belongs
     */
    public ASTTreeItem findItem(Node node) {

        Objects.requireNonNull(node, "Cannot find a null item");

        Iterator<Node> pathToNode = reverse(parentIterator(node, true));

        if (pathToNode.next() != getValue()) {
            // this node is not the root of the tree
            // to which the node we're looking for belongs
            return null;
        }

        ASTTreeItem current = this;

        while (pathToNode.hasNext()) {
            Node currentNode = pathToNode.next();

            current = current.getChildren().stream()
                             .filter(item -> item.getValue() == currentNode)
                             .findAny()
                             .map(ASTTreeItem.class::cast)
                             .get(); // theoretically, this cannot fail, since we use reference identity

        }

        return current;
    }


    /** Builds an ASTTreeItem recursively from a node. */
    public static ASTTreeItem getRoot(Node n) {
        ASTTreeItem item = new ASTTreeItem(n);
        if (n.jjtGetNumChildren() > 0) {
            for (int i = 0; i < n.jjtGetNumChildren(); i++) {
                item.getChildren().add(getRoot(n.jjtGetChild(i)));
            }
        }
        return item;
    }


}
