/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

        grammar.addCommonClass("code");
        grammar.addCommonClass(languageName);
    }


    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        Matcher matcher = grammar.getMatcher(text);
        int lastKwEnd = 0;

        final Set<String> onlyLang = Collections.singleton(languageName);
        try {
            while (matcher.find()) {
                Set<String> styleClasses = grammar.getCssClassesOfLastGroup(matcher);

                builder.add(onlyLang, matcher.start() - lastKwEnd);
                builder.add(styleClasses, matcher.end() - matcher.start());

                lastKwEnd = matcher.end();
            }
        } catch (StackOverflowError ignored) {
            // matcher.find overflowed, might happen when coloring ginormous files with incorrect language
        }

        // add the remainder
        builder.add(onlyLang, text.length() - lastKwEnd);

        return builder.create();
    }


    @Override
    public final String getLanguageTerseName() {
        return languageName;
    }


    /**
     * Returns a regex alternation for the given words.
     * The words must not begin with an escaped character.
     *
     * @param alternatives Words to join
     */
    protected static String alternation(String[] alternatives) {
        // first characters of each alternative, to optimise the regex
        String firstChars = Arrays.stream(alternatives)
                                  .map(s -> s.substring(0, 1))
                                  .distinct()
                                  .reduce((s1, s2) -> s1 + s2)
                                  .get();

        String alt = "(?=[" + firstChars + "])(?:" + String.join("|", alternatives) + ")";

        return asWord(alt);
    }


    protected static String asWord(String regex) {
        return "(?:\\b" + regex + "\\b)";
    }

    /**
     * Gets a builder to make a grammar to build a highlighter.
     *
     * @param cssClass The css class of the first pattern
     * @param pattern  The first pattern
     *
     * @return A builder
     */
    protected static RegexHighlightGrammarBuilder grammarBuilder(Collection<String> cssClass, String pattern) {
        return new RegexHighlightGrammarBuilder(cssClass, pattern);
    }


    /**
     * Builds a highlight grammar in a concise way.
     */
    protected static final class RegexHighlightGrammarBuilder {

        private Map<String, String> groupNameToRegex = new LinkedHashMap<>();
        private Map<String, Set<String>> groupNameToCssClasses = new LinkedHashMap<>();


        RegexHighlightGrammarBuilder(Collection<String> cssClass, String regex) {
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
        public RegexHighlightGrammarBuilder or(Collection<String> cssClass, String regex) {
            String groupName = RandomStringUtils.randomAlphabetic(8);
            groupNameToRegex.put(groupName, regex);
            groupNameToCssClasses.put(groupName, new HashSet<>(cssClass));
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


        private Map<String, Set<String>> getGroupNamesToCssClasses() {
            return Collections.unmodifiableMap(groupNameToCssClasses);
        }
    }


    /**
     * Describes the tokens of the language that should be colored with a regular expression.
     */
    protected static class RegexHighlightGrammar {

        private final Pattern pattern;
        private final Map<String, Set<String>> namesToCssClass;


        RegexHighlightGrammar(Pattern pattern, Map<String, Set<String>> namesToCssClass) {
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
         * Adds a css class to all the tokens described by this grammar.
         *
         * @param css The css class to add
         */
        void addCommonClass(String css) {
            namesToCssClass.values().forEach(e -> e.add(css));
        }


        /**
         * Gets the css class that should be applied to the last matched group (token), according to this grammar.
         *
         * @param matcher The matcher in which to look
         *
         * @return The name of the css class corresponding to the token
         */
        Set<String> getCssClassesOfLastGroup(Matcher matcher) {
            for (Entry<String, Set<String>> groupToClass : namesToCssClass.entrySet()) {
                if (matcher.group(groupToClass.getKey()) != null) {
                    return groupToClass.getValue();
                }
            }

            return Collections.emptySet();
        }

    }

}
