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

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SpanBound;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;

import javafx.concurrent.Task;

/**
 * Language-specific engine for syntax highlighting. The highlighter assigns classes to
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class SimpleRegexSyntaxHighlighter implements SyntaxHighlighter {

    private final RegexHighlightGrammar grammar;
    private final String languageName;


    /**
     * Creates a highlighter given a name for the language and a "regex grammar".
     *
     * @param languageName The language name
     * @param grammar      The grammar
     */
    protected SimpleRegexSyntaxHighlighter(String languageName, final RegexHighlightGrammar grammar) {
        this.grammar = grammar;
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
        Matcher matcher = grammar.getPattern().matcher(text);
        int lastKwEnd = 0;

        try {
            while (matcher.find()) {
                String styleClass = grammar.getCssClassOfLastGroup(matcher);
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


    @Override
    public final String getLanguageTerseName() {
        return languageName;
    }


    /**
     * Gets a builder to make a grammar to build a highlighter.
     *
     * @param cssClass The css class of the first pattern
     * @param pattern  The first pattern
     *
     * @return A builder
     */
    protected static RegexHighlightGrammarBuilder grammarBuilder(String cssClass, String pattern) {
        return new RegexHighlightGrammarBuilder(cssClass, pattern);
    }


    protected static class RegexHighlightGrammarBuilder {

        private Map<String, String> regexToClasses = new LinkedHashMap<>();


        RegexHighlightGrammarBuilder(String cssClass, String regex) {
            or(cssClass, regex);
        }


        /**
         * Adds a branch to the alternation (...|pattern). Order is important.
         *
         * @param cssClass css class that the matching regions should bear
         * @param regex    Regex pattern
         *
         * @return The same builder
         */
        public RegexHighlightGrammarBuilder or(String cssClass, String regex) {
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


        /**
         * Builds the grammar.
         *
         * @return A new grammar
         */
        public RegexHighlightGrammar create() {
            return new RegexHighlightGrammar(Pattern.compile(getRegexString()),
                                             getGroupNamesToCssClasses());
        }


        /**
         * Builds the grammar.
         *
         * @param flags Regex compilation flags
         *
         * @return A new grammar
         */
        public RegexHighlightGrammar create(int flags) {
            return new RegexHighlightGrammar(Pattern.compile(getRegexString(), flags),
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

    /**
     * Describes the tokens of the language that should be colored with a regular expression.
     */
    protected static class RegexHighlightGrammar {

        private final Pattern pattern;
        private final Map<String, String> namesToCssClass;


        public RegexHighlightGrammar(Pattern pattern, Map<String, String> namesToCssClass) {
            this.pattern = pattern;
            this.namesToCssClass = namesToCssClass;
        }


        /**
         * Pattern describing the tokens.
         *
         * @return The pattern
         */
        public Pattern getPattern() {
            return pattern;
        }


        /**
         * Gets the css class that should be applied to the last matched group (token), according to this grammar.
         *
         * @param matcher The matcher in which to look
         *
         * @return The name of the css class corresponding to the token
         */
        public String getCssClassOfLastGroup(Matcher matcher) {
            for (Entry<String, String> groupToClass : namesToCssClass.entrySet()) {
                if (matcher.group(groupToClass.getKey()) != null) {
                    return groupToClass.getValue();
                }
            }

            return "";
        }

    }

}
