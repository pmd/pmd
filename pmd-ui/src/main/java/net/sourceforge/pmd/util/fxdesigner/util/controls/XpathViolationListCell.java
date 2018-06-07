/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;


import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.ConvenienceNodeWrapper;

import javafx.scene.control.ListCell;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XpathViolationListCell extends ListCell<ConvenienceNodeWrapper> {
    private static final Pattern TRUNCATION_PATTERN = Pattern.compile("\\R.*$", Pattern.DOTALL);

    @Override
    protected void updateItem(ConvenienceNodeWrapper item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            String text = TRUNCATION_PATTERN.matcher(item.getNodeText()).replaceFirst("...");
            setText("(l. " + item.getNode().getBeginLine() + ", c. " + item.getNode().getBeginColumn() + "): " + text);
        }
    }

}
