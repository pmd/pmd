/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javafx.concurrent.Task;

/**
 * Language-specific engine for syntax highlighting.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SyntaxHighlighter {

    /**
     * Gets the terse name of the language this highlighter cares for. That's used as a css class for text regions.
     *
     * @return The terse name of the language
     */
    String getLanguageTerseName();


    /**
     * Schedules a syntax highlighting update task and returns it.
     *
     * @param text     Text on which to compute the task.
     * @param executor Task executor service
     *
     * @return The scheduled task
     */
    Task<List<SpanBound>> computeHighlightingAsync(String text, ExecutorService executor);
}
