/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.ListCell;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XpathViolationListCell extends ListCell<Node> {


    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString() + " (l. " + item.getBeginLine() + ", c. " + item.getBeginColumn() + ")");
        }
    }
}
