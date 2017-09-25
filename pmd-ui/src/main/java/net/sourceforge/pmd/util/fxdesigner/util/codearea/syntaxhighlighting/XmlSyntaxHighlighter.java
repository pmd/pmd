/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XmlSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {


    public static final SyntaxHighlighter INSTANCE
        = builder("xml",
                  "multi-line-comment", "<!--.*?-->")
        .or("cdata-tag", "<!\\[CDATA\\[|]]>")
        .or("cdata-content", "(?<=<!\\[CDATA\\[).*?(?=]]>)")
        .or("xml-prolog", "<[?]xml.*[?]>")
        .or("lt-gt", "</?|/?>")
        .or("tag-name", "\\b(?<=(</?))[\\w:]++")
        .or("attribute-name", "\\w+(?=\\s*=\\s*[\"'])")
        .or("string", "('([^'\\\\]|\\\\.)*')|(\"([^\"\\\\]|\\\\.)*\")")
        .create(Pattern.DOTALL);


    private XmlSyntaxHighlighter(String languageName, Pattern pattern, Map<String, String> namesToCssClass) {
        super(languageName, pattern, namesToCssClass);
    }
}
