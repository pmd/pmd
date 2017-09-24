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
 * Syntax highlighter for Java.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class JavaSyntaxHighlighter extends SyntaxHighlighter {


    private static final String[] KEYWORDS = new String[] {
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while",
        };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String CLASS_IDENT_PATTERN = "\\b[A-Z][\\w_$]*\\b";
    private static final String NUMBER_PATTERN = "\\b\\d+[fdlFDL]*\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String SINGLELINE_COMMENT_PATTERN = "//[^\n]*";
    private static final String MULTILINE_COMMENT_PATTERN = "/\\*.*?\\*/";
    private static final String ANNOTATION_PATTERN = "@[\\w]+";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<SINGLELINECOMMENT>" + SINGLELINE_COMMENT_PATTERN + ")"
            + "|(?<MULTILINECOMMENT>" + MULTILINE_COMMENT_PATTERN + ")"
            + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<CLASSIDENT>" + CLASS_IDENT_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")",
        Pattern.DOTALL
    );


    @Override
    public Map<String, String> getGroupNameToCssClass() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("SINGLELINECOMMENT", "single-line-comment");
        map.put("MULTILINECOMMENT", "multi-line-comment");
        map.put("KEYWORD", "keyword");
        map.put("PAREN", "paren");
        map.put("BRACE", "brace");
        map.put("NUMBER", "number");
        map.put("BRACKET", "bracket");
        map.put("SEMICOLON", "semicolon");
        map.put("CLASSIDENT", "class-ident");
        map.put("STRING", "string");
        map.put("ANNOTATION", "annotation");
        return Collections.unmodifiableMap(map);
    }


    @Override
    public Pattern getTokenizerPattern() {
        return PATTERN;
    }


    @Override
    public String getCssFileIdentifier() {
        return JavaSyntaxHighlighter.class.getResource("java.css").toExternalForm();
    }
}
