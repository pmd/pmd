/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Represents a tree item (data, not UI) in the ast TreeView.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTTreeItem extends TreeItem<Node> {


    private ASTTreeItem(Node n) {
        super(n);
        setExpanded(true);
    }


    public ASTTreeItem findItem(Node node) {
        if (this.getValue().equals(node)) {
            return this;
        }

        ObservableList<TreeItem<Node>> children = this.getChildren();
        ASTTreeItem found;
        for (TreeItem<Node> child : children) {
            found = ((ASTTreeItem) child).findItem(node);
            if (found != null) {
                return found;
            }
        }

        return null;
    }


    /** Builds an ASTTreeItem recursively from a node. */
    static ASTTreeItem getRoot(Node n) {
        ASTTreeItem item = new ASTTreeItem(n);
        if (n.jjtGetNumChildren() > 0) {
            for (int i = 0; i < n.jjtGetNumChildren(); i++) {
                item.getChildren().add(getRoot(n.jjtGetChild(i)));
            }
        }
        return item;
    }


}
