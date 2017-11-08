/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BOOLEAN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACE;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACKET;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.KEYWORD;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.PAREN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SEMICOLON;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SINGLEL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.STRING;

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

    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(SINGLEL_COMMENT.css, "//[^\r\n]*")
        .or(MULTIL_COMMENT.css, "/\\*.*?\\*/")
        .or(KEYWORD.css, "\\b(?i)(?=[" + KEYWORDS_START_CHARS + "])(" + String.join("|", KEYWORDS) + ")\\b")
        .or(PAREN.css, "[()]")
        .or(BRACE.css, "[{}]")
        .or(BRACKET.css, "[\\[]]")
        .or(SEMICOLON.css, ";")
        .or(BOOLEAN.css, "\\b(?i)true|false\\b")
        .or(STRING.css, "'[^'\\\\]*(\\\\.[^'\\\\]*)*'")
        .create(Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public ApexSyntaxHighlighter() {
        super("apex", GRAMMAR);
    }


}
