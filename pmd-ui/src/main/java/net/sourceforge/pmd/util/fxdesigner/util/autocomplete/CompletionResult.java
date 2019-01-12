/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import javafx.scene.text.TextFlow;


/**
 * XPath suggestion result.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class CompletionResult implements Comparable<CompletionResult> {
    private final int score;
    private final String suggestion;
    private final TextFlow textFlow;


    CompletionResult(int score, String suggestion, TextFlow textFlow) {
        this.score = score;
        this.suggestion = suggestion;
        this.textFlow = textFlow;
    }


    /** Suggested node name. */
    public String getNodeName() {
        return suggestion;
    }


    /**
     * Formatted TextFlow with the match regions highlighted.
     */
    public TextFlow getTextFlow() {
        return textFlow;
    }


    /** Relevance score of this result. */
    int getScore() {
        return score;
    }


    @Override
    public int compareTo(CompletionResult o) {
        return Integer.compare(score, o.score);
    }
}
