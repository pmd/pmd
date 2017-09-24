/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathSyntaxHighlighter extends SyntaxHighlighter {

    private static final String[] AXIS_NAMES = new String[] {
        "self", "child", "attribute", "descendant", "descendant-or-self", "ancestor",
        "ancestor-or-self", "following", "following-sibling", "namespace", "parent",
        "preceding-sibling",
        };
    private static final String[] KEYWORDS = new String[] {
        "or", "and", "not",
        };

    private static final String AXIS_PATTERN = "(" + String.join("|", AXIS_NAMES) + ")(?=::)";
    private static final String KEYWORD_PATTERN = "(" + String.join("|", KEYWORDS) + ")";
    private static final String PAREN_PATTERN = "[()]";
    private static final String PATH_PATTERN = "/";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "\\(:.*:\\)";
    private static final String ATTRIBUTE_PATTERN = "@[\\w]+";
    private static final String FUNCTION_PATTERN = "[\\w-]+?(?=\\()";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<ATTRIBUTE>" + ATTRIBUTE_PATTERN + ")"
            + "|(?<AXIS>" + AXIS_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
            + "|(?<PATH>" + PATH_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );


    @Override
    public Map<String, String> getGroupNameToCssClass() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ATTRIBUTE", "xpath-attribute");
        map.put("FUNCTION", "xpath-function");
        map.put("PATH", "xpath-path");
        map.put("PAREN", "xpath-paren");
        map.put("BRACKET", "xpath-bracket");
        map.put("AXIS", "xpath-axis");
        map.put("KEYWORD", "xpath-keyword");
        map.put("STRING", "xpath-string");
        map.put("COMMENT", "xpath-comment");
        return Collections.unmodifiableMap(map);
    }


    @Override
    public Pattern getTokenizerPattern() {
        return PATTERN;
    }


    @Override
    public String getCssFileIdentifier() {
        return JavaSyntaxHighlighter.class.getResource("xpath.css").toExternalForm();
    }


    public static void main(String[] a) {
        Matcher matcher = Pattern.compile(AXIS_PATTERN).matcher("following-sibling");

        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

}
