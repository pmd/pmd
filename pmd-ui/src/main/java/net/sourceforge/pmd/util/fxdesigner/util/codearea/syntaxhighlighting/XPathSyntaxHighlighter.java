/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACKET;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.KEYWORD;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.PAREN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.SINGLEL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.STRING;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {

    private static final String[] AXIS_NAMES = new String[] {
        "self", "child", "attribute", "descendant", "descendant-or-self", "ancestor",
        "ancestor-or-self", "following", "following-sibling", "namespace", "parent",
        "preceding-sibling",
        };

    private static final String[] KEYWORDS = new String[] {
        "or", "and", "not",
        };


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder("attribute", "@[\\w]+")
        .or("axis", "(" + String.join("|", AXIS_NAMES) + ")(?=::)")
        .or(KEYWORD.css, "\\b(" + String.join("|", KEYWORDS) + ")\\b")
        .or("function", "[\\w-]+?(?=\\()")
        .or("path", "//?")
        .or(PAREN.css, "[()]")
        .or(BRACKET.css, "[\\[\\]]")
        .or(STRING.css, "('([^'\\\\]|\\\\.)*')|(\"([^\"\\\\]|\\\\.)*\")")
        .or(SINGLEL_COMMENT.css, "\\(:.*:\\)")
        .create();


    public XPathSyntaxHighlighter() {
        super("xpath", GRAMMAR);
    }


}
