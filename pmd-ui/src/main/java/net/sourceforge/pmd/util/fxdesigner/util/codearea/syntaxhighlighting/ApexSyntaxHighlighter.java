/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;

/**
 * Syntax highlighter for Apex.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ApexSyntaxHighlighter extends SyntaxHighlighter {


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


    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String SINGLELINE_COMMENT_PATTERN = "//[^\r\n]*";
    private static final String MULTILINE_COMMENT_PATTERN = "/\\*.*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<SINGLELINECOMMENT>" + SINGLELINE_COMMENT_PATTERN + ")"
            + "|(?<MULTILINECOMMENT>" + MULTILINE_COMMENT_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")",
        Pattern.DOTALL);


    @Override
    public Map<String, String> getGroupNameToCssClass() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("SINGLELINECOMMENT", "single-line-comment");
        map.put("MULTILINECOMMENT", "multi-line-comment");
        map.put("KEYWORD", "keyword");
        map.put("PAREN", "paren");
        map.put("BRACE", "brace");
        map.put("BRACKET", "bracket");
        map.put("SEMICOLON", "semicolon");
        map.put("STRING", "string");
        return Collections.unmodifiableMap(map);
    }


    @Override
    public Pattern getTokenizerPattern() {
        return PATTERN;
    }


    @Override
    public String getCssFileIdentifier() {
        return ApexSyntaxHighlighter.class.getResource("apex.css").toExternalForm();
    }
}
