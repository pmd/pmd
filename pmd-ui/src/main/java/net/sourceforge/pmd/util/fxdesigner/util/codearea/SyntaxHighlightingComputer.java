/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.SyntaxHighlighter;

import javafx.concurrent.Task;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SyntaxHighlightingComputer {

    private final SyntaxHighlighter highlighter;


    public SyntaxHighlightingComputer(SyntaxHighlighter highlighter) {
        this.highlighter = highlighter;
    }


    public String getCssFileIdentifier() {
        return highlighter.getCssFileIdentifier();
    }


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
        Matcher matcher = highlighter.getTokenizerPattern().matcher(text);
        int lastKwEnd = 0;


        while (matcher.find()) {
            String styleClass = null;
            for (Entry<String, String> groupToClass : highlighter.getGroupNameToCssClass().entrySet()) {
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
        return updated;
    }
}
