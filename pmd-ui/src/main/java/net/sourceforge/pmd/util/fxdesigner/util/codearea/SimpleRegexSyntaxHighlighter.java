/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.concurrent.Task;

/**
 * Language specific engine for syntax highlighting.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SimpleRegexSyntaxHighlighter implements SyntaxHighlighter {

    private final Pattern pattern;
    private final Map<String, String> namesToCssClass;
    private final String languageName;


    protected SimpleRegexSyntaxHighlighter(String languageName, Pattern pattern, Map<String, String> namesToCssClass) {
        this.pattern = pattern;
        this.namesToCssClass = namesToCssClass;
        this.languageName = languageName;
    }


    @Override
    public final Task<List<SpanBound>> computeHighlightingAsync(String text, ExecutorService executor) {
        Task<List<SpanBound>> task = new Task<List<SpanBound>>() {
            @Override
            protected List<SpanBound> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        if (!executor.isShutdown()) {
            executor.execute(task);
        }
        return task;
    }


    private List<SpanBound> computeHighlighting(String text) {
        List<SpanBound> updated = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;

        try {
            while (matcher.find()) {
                String styleClass = getCssClassOfLastGroup(matcher);
                updated.add(new SpanBound(lastKwEnd, Collections.singleton(languageName), true));
                updated.add(new SpanBound(matcher.start(), Collections.emptySet(), false));
                updated.add(new SpanBound(matcher.start(), new HashSet<>(Arrays.asList(languageName, styleClass)), true));
                updated.add(new SpanBound(matcher.end(), new HashSet<>(Arrays.asList(languageName, styleClass)), false));
                lastKwEnd = matcher.end();
            }
        } catch (StackOverflowError so) {
            // matcher.find overflowed, may happen when coloring ginormous files with incorrect language
        }
        return updated;
    }


    private String getCssClassOfLastGroup(Matcher matcher) {
        for (Entry<String, String> groupToClass : namesToCssClass.entrySet()) {
            if (matcher.group(groupToClass.getKey()) != null) {
                return groupToClass.getValue();
            }
        }

        return "";
    }


    @Override
    public final String getLanguageTerseName() {
        return languageName;
    }


    public static RegexHighlighterBuilder builder(String languageName, String cssClass, String pattern) {
        return new RegexHighlighterBuilder(languageName, cssClass, pattern);
    }


    public static class RegexHighlighterBuilder {

        private final String language;
        private Map<String, String> regexToClasses = new LinkedHashMap<>();


        RegexHighlighterBuilder(String languageName, String cssClass, String regex) {
            or(cssClass, regex);
            language = languageName;
        }


        /**
         * Adds a branch to the alternation (...|pattern). Order is important.
         *
         * @param cssClass css class that the matching regions should bear
         * @param regex    Regex pattern
         *
         * @return The same builder
         */
        public RegexHighlighterBuilder or(String cssClass, String regex) {
            regexToClasses.put(regex, cssClass);
            return this;
        }


        private String namedGroup(String name, String regex) {
            return "(?<" + name + ">" + regex + ")";
        }


        private String getGroupNameOf(String regex, String cssClass) {
            return cssClass.replaceAll("[^a-zA-Z]", "").toUpperCase();
        }


        private String getRegexString() {

            return String.join("|",
                               regexToClasses.entrySet()
                                             .stream()
                                             .map(e -> namedGroup(getGroupNameOf(e.getKey(), e.getValue()), e.getKey()))
                                             .collect(Collectors.toList()));
        }


        public SyntaxHighlighter create() {
            return new SimpleRegexSyntaxHighlighter(language,
                                                    Pattern.compile(getRegexString()),
                                                    getGroupNamesToCssClasses());
        }


        /**
         * Builds the syntax highlighter.
         *
         * @param flags Regex compilation flags
         *
         * @return A new highlighter
         */
        public SyntaxHighlighter create(int flags) {
            return new SimpleRegexSyntaxHighlighter(language,
                                                    Pattern.compile(getRegexString(), flags),
                                                    getGroupNamesToCssClasses());
        }


        private Map<String, String> getGroupNamesToCssClasses() {
            Map<String, String> result = new HashMap<>();

            for (Entry<String, String> e : regexToClasses.entrySet()) {
                result.put(getGroupNameOf(e.getKey(), e.getValue()), e.getValue());
            }

            return result;
        }
    }

}
