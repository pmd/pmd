/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.util.Collections;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;

import javafx.collections.FXCollections;
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
    }


    void expandAll() {
        expandAllHelper(this);
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


    private static void expandAllHelper(TreeItem<Node> item) {
        item.setExpanded(true);
        if (item.getChildren().size() > 0) {
            for (TreeItem<Node> child : item.getChildren()) {
                expandAllHelper(child);
            }
        }
    }
}
