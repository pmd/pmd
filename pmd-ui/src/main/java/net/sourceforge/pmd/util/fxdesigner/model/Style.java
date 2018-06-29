/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.Locale;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class Style {

    private Style() {

    }

    public static TextFlow highlight(String text, String filter) {
        //Gets Locale from user PC
        int filterIndex = text.toLowerCase(Locale.getDefault()).indexOf(filter.toLowerCase(Locale.getDefault()));

        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + filter.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length())); //instead of "filter" to keep all "case sensitive"
        textFilter.setFill(Color.ORANGE);
        return new TextFlow(textBefore, textFilter, textAfter);
    }




}
