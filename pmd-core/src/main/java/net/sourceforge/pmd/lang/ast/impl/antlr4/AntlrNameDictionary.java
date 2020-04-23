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

    private final String[] xpathNames;
    private final Vocabulary vocabulary;

    public AntlrNameDictionary(Vocabulary vocab) {
        this.vocabulary = vocab;

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
            }
            if (name == null) {
                name = String.valueOf(i);
            }

            xpathNames[i] = "T-" + name;
        }

        this.xpathNames = xpathNames;


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

        case "=": return "eq";
        case "==": return "double-eq";
        case "===": return "triple-eq";
        case "!=": return "not-eq";

        case ">>": return "double-gt";
        case "<<": return "double-lt";
        case ">>>": return "triple-gt";
        case "<<<": return "triple-lt";

        case "*": return "star";
        case "**": return "double-star";

        case "+": return "plus";
        case "-": return "minus";

        case "->": return "rarrow";
        case "<-": return "larrow";
        }
        return null;
    }

    public @NonNull String getXPathName(int tokenType) {
        if (tokenType >= 0 && tokenType < xpathNames.length) {
            return xpathNames[tokenType];
        }

        if (tokenType == Token.EOF) {
            return "EOF";
        }

        throw new IllegalArgumentException("I don't know token type " + tokenType);
    }

    public int getMaxTokenType() {
        return vocabulary.getMaxTokenType();
    }
}
