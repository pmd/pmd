/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


/**
 * Language-specific engine for syntax highlighting. This implementation tokenises the text using regex, and assigns a
 * specific CSS class to every found token. The whole text also receives a style class named after the language of the
 * tokenizer (e.g. "xml" or "java"). Styling of each class is then done in stylesheets.
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
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        Matcher matcher = grammar.getMatcher(text);
        int lastKwEnd = 0;

        try {
            while (matcher.find()) {
                String styleClass = grammar.getCssClassOfLastGroup(matcher);

                builder.add(Collections.singleton(languageName), matcher.start() - lastKwEnd);
                builder.add(Arrays.asList(languageName, styleClass), matcher.end() - matcher.start());

                lastKwEnd = matcher.end();
            }
        } catch (StackOverflowError so) {
            // matcher.find overflowed, might happen when coloring ginormous files with incorrect language
        }

        if (lastKwEnd == 0) { // no spans found/ no text
            builder.add(Collections.emptySet(), text.length());
        }

        return builder.create();
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


    /**
     * Builds a highlight grammar in a concise way.
     */
    protected static class RegexHighlightGrammarBuilder {

        private Map<String, String> groupNameToRegex = new LinkedHashMap<>();
        private Map<String, String> groupNameToCssClass = new LinkedHashMap<>();


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
            String groupName = RandomStringUtils.randomAlphabetic(8);
            groupNameToRegex.put(groupName, regex);
            groupNameToCssClass.put(groupName, cssClass);
            return this;
        }


        private String namedGroup(String name, String regex) {
            return "(?<" + name + ">(?:" + regex + "))";
        }


        private String getRegexString() {
            return String.join("|", groupNameToRegex.entrySet()
                                                    .stream()
                                                    .map(e -> namedGroup(e.getKey(), e.getValue()))
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
            return Collections.unmodifiableMap(groupNameToCssClass);
        }
    }


    /**
     * Describes the tokens of the language that should be colored with a regular expression.
     */
    protected static class RegexHighlightGrammar {

        private final Pattern pattern;
        private final Map<String, String> namesToCssClass;


        RegexHighlightGrammar(Pattern pattern, Map<String, String> namesToCssClass) {
            this.pattern = pattern;
            this.namesToCssClass = namesToCssClass;
        }


        /**
         * Gets a matcher for the given piece of text.
         *
         * @return A matcher
         */
        Matcher getMatcher(String text) {
            return pattern.matcher(text);
        }


        /**
         * Gets the css class that should be applied to the last matched group (token), according to this grammar.
         *
         * @param matcher The matcher in which to look
         *
         * @return The name of the css class corresponding to the token
         */
        String getCssClassOfLastGroup(Matcher matcher) {
            for (Entry<String, String> groupToClass : namesToCssClass.entrySet()) {
                if (matcher.group(groupToClass.getKey()) != null) {
                    return groupToClass.getValue();
                }
            }

            return "";
        }

    }

}
