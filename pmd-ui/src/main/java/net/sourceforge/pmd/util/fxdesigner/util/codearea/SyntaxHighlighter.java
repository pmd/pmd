/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;

/**
 * Language specific engine for syntax highlighting.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class SyntaxHighlighter {

    /**
     * Schedules a syntax highlighting update task and returns it.
     *
     * @param text     Text on which to compute the task.
     * @param executor Task executor service
     *
     * @return The scheduled task
     */
    Task<List<SpanBound>> computeHighlightingAsync(String text, ExecutorService executor) {
        Task<List<SpanBound>> task = new Task<List<SpanBound>>() {
            @Override
            protected List<SpanBound> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }


    private List<SpanBound> computeHighlighting(String text) {
        List<SpanBound> updated = new ArrayList<>();
        Matcher matcher = getTokenizerPattern().matcher(text);
        int lastKwEnd = 0;

        try {
            while (matcher.find()) {
                String styleClass = null;
                for (Entry<String, String> groupToClass : getGroupNameToCssClass().entrySet()) {
                    if (matcher.group(groupToClass.getKey()) != null) {
                        styleClass = groupToClass.getValue();
                        break;
                    }
                }
                assert styleClass != null;
                updated.add(new SpanBound(lastKwEnd, Collections.emptySet(), true));
                updated.add(new SpanBound(matcher.start(), Collections.emptySet(), false));
                updated.add(new SpanBound(matcher.start(), Collections.singleton(styleClass), true));
                updated.add(new SpanBound(matcher.end(), Collections.singleton(styleClass), false));
                lastKwEnd = matcher.end();
            }
        } catch (StackOverflowError so) {
            // matcher.find overflowed, possible when handling huge files with incorrect language
        }
        return updated;
    }


    /**
     * Gets an ordered map of regex patterns to the CSS class that must be applied. The map must be ordered by
     * priority.
     *
     * @return An ordered map
     */
    public abstract Map<String, String> getGroupNameToCssClass();


    /**
     * Gets the pattern used to tokenize the text. Token groups must be named (syntax is {@code (?<GROUP_NAME>..)}).
     * Tokens are mapped to a css class using the {@link #getGroupNameToCssClass()} method.
     *
     * @return The tokenizer pattern
     */
    public abstract Pattern getTokenizerPattern();


    /**
     * Gets the identifier of the resource file containing appropriate css. This string must be suitable for use within
     * a call to {@code getStyleSheets().add()}.
     *
     * @return The identifier of a css file
     */
    public abstract String getCssFileIdentifier();


}
