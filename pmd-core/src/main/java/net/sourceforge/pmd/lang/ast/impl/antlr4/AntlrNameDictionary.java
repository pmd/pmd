/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.Arrays;
import java.util.stream.Stream;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Stores the XPath name of antlr terminals. I found no simple way to
 * give names to punctuation (we could add a lexer rule, but it may
 * conflict with other tokens). So their names are hardcoded here.
 *
 * <p>Terminal names start with {@code "T-"} in XPath to avoid conflicts
 * with other stuff.
 */
public class AntlrNameDictionary {

    public static final int ROOT_RULE_IDX = -1;

    private final String[] terminalXpathNames;
    private final String[] nonTermXpathNames;
    private final Vocabulary vocabulary;

    public AntlrNameDictionary(Vocabulary vocab, String[] ruleNames) {
        this.vocabulary = vocab;

        nonTermXpathNames = new String[ruleNames.length];
        for (int i = 0; i < nonTermXpathNames.length; i++) {
            nonTermXpathNames[i] = StringUtils.capitalize(ruleNames[i]);
        }

        String[] xpathNames = new String[vocab.getMaxTokenType()];
        for (int i = 0; i < xpathNames.length; i++) {
            String name = vocab.getSymbolicName(i);


            if (name == null) {
                name = vocab.getLiteralName(i);

                if (name != null) {
                    // cleanup literal name
                    name = name.substring(1, name.length() - 1);
                    if (!StringUtils.isAlphanumeric(name)) {
                        name = maybePunctName(name);
                    }
                }
            } else {
                assert name.matches("[a-zA-Z][\\w_-]+"); // must be a valid xpath name
            }
            if (name == null) {
                name = String.valueOf(i);
            }

            xpathNames[i] = "T-" + name;
        }

        this.terminalXpathNames = xpathNames;


        assert Stream.of(xpathNames).distinct().count() == xpathNames.length
            : "Duplicate names in " + Arrays.toString(xpathNames);
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    protected @Nullable String maybePunctName(String s) {
        // these are hardcoded, but it's overridable
        // here we try to avoid semantic overtones, because
        // a-priori the same terminal may mean several things
        // in different contexts.
        switch (s) {
        case "!": return "bang";
        case "!!": return "double-bang";

        case "?": return "question";
        case "??": return "double-question";
        case "?:": return "elvis";
        case "?.": return "question-dot";

        case ":": return "colon";
        case ";": return "semi";
        case ",": return "comma";

        case "(": return "lparen";
        case ")": return "rparen";
        case "[": return "lbracket";
        case "]": return "rbracket";
        case "{": return "lbrace";
        case "}": return "rbrace";

        case "_": return "underscore";

        case ".": return "dot";
        case "..": return "double-dot";
        case "...": return "ellipsis";

        case "@": return "at-symbol";
        case "$": return "dollar";
        case "&": return "amp";

        case "\\": return "backslash";
        case "/": return "slash";
        case "//": return "double-slash";
        case "`": return "backtick";
        case "'": return "squote";
        case "\"": return "dquote";

        case ">": return "gt";
        case ">=": return "ge";
        case "<": return "lt";
        case "<=": return "le";

        case ">>": return "double-gt";
        case "<<": return "double-lt";
        case ">>>": return "triple-gt";
        case "<<<": return "triple-lt";

        case "=": return "eq";
        case "==": return "double-eq";
        case "===": return "triple-eq";
        case "!=": return "not-eq";

        case "*": return "star";
        case "**": return "double-star";

        case "+": return "plus";
        case "-": return "minus";

        case "->": return "rarrow";
        case "<-": return "larrow";
        }
        return null;
    }

    public @NonNull String getXPathNameOfToken(int tokenType) {
        if (tokenType >= 0 && tokenType < terminalXpathNames.length) {
            return terminalXpathNames[tokenType];
        }

        if (tokenType == Token.EOF) {
            return "EOF";
        }

        throw new IllegalArgumentException("I don't know token type " + tokenType);
    }

    public @NonNull String getXPathNameOfRule(int idx) {
        if (idx >= 0 && idx < nonTermXpathNames.length) {
            return nonTermXpathNames[idx];
        } else if (idx == ROOT_RULE_IDX) {
            return "FileRoot";
        }
        throw new IllegalArgumentException("I don't know rule type " + idx);
    }

    public int getMaxRuleIndex() {
        return nonTermXpathNames.length;
    }

    public int getMaxTokenType() {
        return vocabulary.getMaxTokenType();
    }
}
