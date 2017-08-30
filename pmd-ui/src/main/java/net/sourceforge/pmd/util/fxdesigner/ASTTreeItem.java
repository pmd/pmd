/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.TreeItem;

/**
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


    @Override
    public String toString() {
        String s = getValue().toString();
        if (getValue().getImage() != null) {
            s += " \"" + getValue().getImage() + "\"";
        }
        return s;
    }


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
