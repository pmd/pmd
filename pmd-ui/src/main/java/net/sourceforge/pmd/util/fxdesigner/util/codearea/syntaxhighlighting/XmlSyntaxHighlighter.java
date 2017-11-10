/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_ATTRIBUTE_NAME;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_CDATA_CONTENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_CDATA_TAG;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_LT_GT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_PROLOG;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XML_TAG_NAME;

import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XmlSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(MULTIL_COMMENT.css, "<!--.*?-->")
        .or(XML_CDATA_TAG.css, "<!\\[CDATA\\[|]]>")
        .or(XML_CDATA_CONTENT.css, "(?<=<!\\[CDATA\\[).*?(?=]]>)")
        .or(XML_PROLOG.css, "<\\?xml.*?\\?>")
        .or(XML_LT_GT.css, "</?|/?>")
        .or(XML_TAG_NAME.css, "\\b(?<=(</?))\\w[-\\w:]*")
        .or(XML_ATTRIBUTE_NAME.css, "\\w[-\\w]*(?=\\s*=\\s*[\"'])")
        .or(HighlightClasses.STRING.css, "('([^'<>\\\\]|\\\\.)*')|(\"([^\"<>\\\\]|\\\\.)*\")")
        .create(Pattern.DOTALL);


    public XmlSyntaxHighlighter() {
        super("xml", GRAMMAR);
    }
}
