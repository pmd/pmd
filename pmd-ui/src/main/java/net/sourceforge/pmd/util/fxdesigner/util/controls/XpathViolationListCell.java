/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;


import java.util.Collection;

import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyledDocument;

import net.sourceforge.pmd.util.fxdesigner.util.ConvenienceNodeWrapper;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XpathViolationListCell extends ListCell<ConvenienceNodeWrapper> {

    @Override
    protected void updateItem(ConvenienceNodeWrapper item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(richTextForNode(item));
        }
    }


    private TextFlow richTextForNode(ConvenienceNodeWrapper node) {
        StyledDocument<Collection<String>, String, Collection<String>> richText = node.getNodeRichText();

        TextFlow result = new TextFlow();
        int lastSpanEnd = 0;
        for (StyleSpan<Collection<String>> span : richText.getStyleSpans(0, richText.length())) {
            String spanText = richText.getText(lastSpanEnd, lastSpanEnd + span.getLength());
            int truncateTo = spanText.indexOf("\n");

            Text text = new Text(truncateTo < 0 ? spanText : spanText.substring(0, truncateTo));
            text.getStyleClass().addAll(span.getStyle());
            text.getStyleClass().removeIf(s -> s.endsWith("-highlight"));

            result.getChildren().add(text);

            lastSpanEnd += text.getText().length();
            if (truncateTo > 0) {
                break;
            }
        }

        // we truncated
        if (lastSpanEnd < richText.length()) {
            result.getChildren().add(new Text("..."));
        }

        return result;
    }

}
