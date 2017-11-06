/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Arrays;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * Syntax highlighter for Apex.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ApexSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final String[] KEYWORDS = new String[] {
        "abstract", "activate", "and", "any", "array", "as",
        "asc", "autonomous", "begin", "bigdecimal", "blob",
        "break", "bulk", "by", "byte", "case", "cast", "catch",
        "char", "class", "collect", "commit", "const", "continue",
        "convertcurrency", "decimal", "default", "delete", "desc",
        "do", "else", "end", "enum", "exception", "exit", "export",
        "extends", "false", "final", "finally", "float", "for", "from",
        "future", "global", "goto", "group", "having", "hint", "if",
        "implements", "import", "inner", "insert", "instanceof",
        "interface", "into", "int", "join", "last_90_days", "last_month",
        "last_n_days", "last_week", "like", "limit", "list", "long",
        "loop", "map", "merge", "new", "next_90_days", "next_month",
        "next_n_days", "next_week", "not", "null", "nulls", "number",
        "object", "of", "on", "or", "outer", "override", "package",
        "parallel", "pragma", "private", "protected", "public", "retrieve",
        "return", "returning", "rollback", "savepoint", "search", "select",
        "set", "short", "sort", "stat", "static", "super", "switch", "synchronized",
        "system", "testmethod", "then", "this", "this_month", "this_week",
        "throw", "today", "tolabel", "tomorrow", "transaction", "trigger",
        "true", "try", "type", "undelete", "update", "upsert", "using",
        "virtual", "webservice", "when", "where", "while", "yesterday",
        "after", "before", "count", "excludes", "first", "includes",
        "last", "order", "sharing", "with",
        };


    /** First characters of the keywords, used to optimise the regex. */
    private static final String KEYWORDS_START_CHARS = Arrays.stream(KEYWORDS)
                                                             .map(s -> s.substring(0, 1))
                                                             .distinct()
                                                             .reduce((s1, s2) -> s1 + s2)
                                                             .get();

    public static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder("single-line-comment", "//[^\r\n]*")
        .or("multi-line-comment", "/\\*.*?\\*/")
        .or("keyword", "\\b(?=[" + KEYWORDS_START_CHARS + "])(" + String.join("|", KEYWORDS) + ")\\b")
        .or("paren", "[()]")
        .or("brace", "[{}]")
        .or("bracket", "[\\[]]")
        .or("semicolon", ";")
        .or("boolean", "true|false")
        .or("string", "'[^'\\\\]*(\\\\.[^'\\\\]*)*'")
        .create(Pattern.DOTALL);

    public ApexSyntaxHighlighter() {
        super("apex", GRAMMAR);
    }

}
