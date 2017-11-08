/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;

import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XmlSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(MULTIL_COMMENT.css, "<!--.*?-->")
        .or("cdata-tag", "<!\\[CDATA\\[|]]>")
        .or("cdata-content", "(?<=<!\\[CDATA\\[).*?(?=]]>)")
        .or("xml-prolog", "<\\?xml.*?\\?>")
        .or("lt-gt", "</?|/?>")
        .or("tag-name", "\\b(?<=(</?))\\w[-\\w:]*")
        .or("attribute-name", "\\w[-\\w]*(?=\\s*=\\s*[\"'])")
        .or(HighlightClasses.STRING.css, "('([^'<>\\\\]|\\\\.)*')|(\"([^\"<>\\\\]|\\\\.)*\")")
        .create(Pattern.DOTALL);


    public XmlSyntaxHighlighter() {
        super("xml", GRAMMAR);
    }
}
