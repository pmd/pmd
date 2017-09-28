/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XmlSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder("multi-line-comment", "<!--.*?-->")
        .or("cdata-tag", "<!\\[CDATA\\[|]]>")
        .or("cdata-content", "(?<=<!\\[CDATA\\[).*?(?=]]>)")
        .or("xml-prolog", "<[?]xml.*[?]>")
        .or("lt-gt", "</?|/?>")
        .or("tag-name", "\\b(?<=(</?))[\\w:]++")
        .or("attribute-name", "\\w+(?=\\s*=\\s*[\"'])")
        .or("string", "('([^'<>\\\\]|\\\\.)*')|(\"([^\"<>\\\\]|\\\\.)*\")")
        .create(Pattern.DOTALL);


    public XmlSyntaxHighlighter() {
        super("xml", GRAMMAR);
    }
}
