/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;


/**
 * Formats the cell for AST nodes in the main AST TreeView.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTTreeCell extends TreeCell<Node> {

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString() + (item.getImage() == null ? "" : " \"" + item.getImage() + "\""));
        }
    }


    public static Callback<TreeView<Node>, ASTTreeCell> callback() {
        return p -> new ASTTreeCell();
    }
}
