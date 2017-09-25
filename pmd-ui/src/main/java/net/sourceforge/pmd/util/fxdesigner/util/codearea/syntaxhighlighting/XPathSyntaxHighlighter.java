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
public class XPathSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {

    private static final String[] AXIS_NAMES = new String[] {
        "self", "child", "attribute", "descendant", "descendant-or-self", "ancestor",
        "ancestor-or-self", "following", "following-sibling", "namespace", "parent",
        "preceding-sibling",
        };
    private static final String[] KEYWORDS = new String[] {
        "or", "and", "not",
        };


    public static final SyntaxHighlighter INSTANCE
        = builder("xpath",
                  "attribute", "@[\\w]+")
        .or("axis", "(" + String.join("|", AXIS_NAMES) + ")(?=::)")
        .or("keyword", "(" + String.join("|", KEYWORDS) + ")")
        .or("function", "[\\w-]+?(?=\\()")
        .or("path", "//?")
        .or("paren", "[()]")
        .or("bracket", "[\\[\\]]")
        .or("string", "'([^'\\\\]|\\\\.)*'")
        .or("single-line-comment", "\\(:.*:\\)")
        .create();


    private XPathSyntaxHighlighter(String languageName, Pattern pattern, Map<String, String> namesToCssClass) {
        super(languageName, pattern, namesToCssClass);
    }


}
