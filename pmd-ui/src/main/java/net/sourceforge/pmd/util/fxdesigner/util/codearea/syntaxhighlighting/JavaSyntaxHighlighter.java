/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.ANNOTATION;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BOOLEAN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACE;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACKET;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.CHAR;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.CLASS_IDENTIFIER;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.KEYWORD;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.NULL;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.NUMBER;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.PAREN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SEMICOLON;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SINGLEL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.STRING;

import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * Syntax highlighter for Java.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class JavaSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final String[] KEYWORDS = {
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
        "opens", "to", "uses", "provides", "var", "with",
        };


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(SINGLEL_COMMENT.css, "//[^\n]*")
        .or(MULTIL_COMMENT.css, "/\\*.*?\\*/")
        .or(PAREN.css, "[()]")
        .or(NUMBER.css, asWord("\\d+[fdlFDL]*"))
        .or(BRACE.css, "[{}]")
        .or(BRACKET.css, "[\\[]]")
        .or(SEMICOLON.css, ";")
        .or(KEYWORD.css, alternation(KEYWORDS))
        .or(STRING.css, "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"")
        .or(CHAR.css, "'(?:[^']|\\\\(?:'|u\\w{4}))'") // char
        .or(NULL.css, asWord("null"))
        .or(BOOLEAN.css, asWord("true|false"))
        .or(ANNOTATION.css, "@[\\w]+")
        .or(CLASS_IDENTIFIER.css, asWord("[A-Z][\\w_$]*"))
        .create(Pattern.DOTALL);


    public JavaSyntaxHighlighter() {
        super("java", GRAMMAR);
    }

}
