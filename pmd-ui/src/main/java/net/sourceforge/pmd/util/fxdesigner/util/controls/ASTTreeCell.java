/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import org.reactfx.value.Val;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;

import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


/**
 * Formats the cell for AST nodes in the main AST TreeView.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTTreeCell extends TreeCell<Node> {

    private final MainDesignerController controller;


    public ASTTreeCell(MainDesignerController controller) {
        this.controller = controller;

        Val.wrap(treeItemProperty())
           .map(ASTTreeItem.class::cast)
           .changes()
           .subscribe(change -> {
               if (change.getOldValue() != null) { // TODO possible race condition here
                   change.getOldValue().treeCellProperty().setValue(null);
               }
               if (change.getNewValue() != null) {
                   change.getNewValue().treeCellProperty().setValue(this);
               }
           });

    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        } else {
            setText(item.toString() + (item.getImage() == null ? "" : " \"" + item.getImage() + "\""));
        }

        // Reclicking the selected node in the ast will scroll back to the node in the editor
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.PRIMARY
                    && getTreeView().getSelectionModel().getSelectedItem().getValue() == item) {
                controller.onNodeItemSelected(item);
                t.consume();
            }
        });

    }
}
