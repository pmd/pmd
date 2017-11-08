/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BOOLEAN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACE;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACKET;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.CHAR;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.KEYWORD;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.NULL;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.NUMBER;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.PAREN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SEMICOLON;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SINGLEL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.STRING;

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
        "do", "strictfp", "goto", "const", "open", 
        "module", "requires", "transitive", "exports", 
        "opens", "to", "uses", "provides", "with",
        };


    /** First characters of the keywords, used to optimise the regex. */
    private static final String KEYWORDS_START_CHARS = Arrays.stream(KEYWORDS)
                                                             .map(s -> s.substring(0, 1))
                                                             .distinct()
                                                             .reduce((s1, s2) -> s1 + s2)
                                                             .get();


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(SINGLEL_COMMENT.css, "//[^\n]*")
        .or(MULTIL_COMMENT.css, "/\\*.*?\\*/")
        .or(PAREN.css, "[()]")
        .or(NUMBER.css, "\\b\\d+[fdlFDL]*\\b")
        .or(BRACE.css, "[{}]")
        .or(BRACKET.css, "[\\[]]")
        .or(SEMICOLON.css, ";")
        .or(KEYWORD.css, "\\b(?=[" + KEYWORDS_START_CHARS + "])(?:" + String.join("|", KEYWORDS) + ")\\b")
        .or(STRING.css, "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"")
        .or(CHAR.css, "'(?:[^']|\\\\(?:'|u\\w{4}))'") // char
        .or(NULL.css, "\\bnull\\b") 
        .or(BOOLEAN.css, "\\btrue|false\\b") 
        .or("annotation", "@[\\w]+")
        .or("class-ident", "\\b[A-Z][\\w_$]*\\b")
        .create(Pattern.DOTALL);


    public JavaSyntaxHighlighter() {
        super("java", GRAMMAR);
    }

}
