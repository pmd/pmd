/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Arrays;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * Syntax highlighter for Java.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class JavaSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final String[] KEYWORDS = new String[] {
        "public", "return", "final", "import", "static", "new",
        "extends", "int", "throws?", "void", "if", "this",
        "private", "class", "else", "case", "package", "abstract",
        "boolean", "break", "byte", "catch", "char", "for",
        "continue", "default", "double", "enum", "finally",
        "float", "implements", "instanceof", "interface", "long",
        "native", "protected", "while", "assert", "short", "super",
        "switch", "synchronized", "transient", "try", "volatile",
        "do", "strictfp", "goto", "const",
        };


    /** First characters of the keywords, used to optimise the regex. */
    private static final String KEYWORDS_START_CHARS = Arrays.stream(KEYWORDS)
                                                             .map(s -> s.substring(0, 1))
                                                             .distinct()
                                                             .reduce((s1, s2) -> s1 + s2)
                                                             .get();


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder("single-line-comment", "//[^\n]*")
        .or("multi-line-comment", "/\\*.*?\\*/")
        .or("annotation", "\\b@[\\w]+")
        .or("paren", "[()]")
        .or("number", "\\b\\d+[fdlFDL]*\\b")
        .or("brace", "[{}]")
        .or("bracket", "[\\[]]")
        .or("semicolon", ";")
        .or("keyword", "\\b(?=[" + KEYWORDS_START_CHARS + "])(" + String.join("|", KEYWORDS) + ")\\b")
        .or("class-ident", "\\b[A-Z][\\w_$]*\\b")
        .or("string", "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"")
        .create(Pattern.DOTALL);


    public JavaSyntaxHighlighter() {
        super("java", GRAMMAR);
    }

}
