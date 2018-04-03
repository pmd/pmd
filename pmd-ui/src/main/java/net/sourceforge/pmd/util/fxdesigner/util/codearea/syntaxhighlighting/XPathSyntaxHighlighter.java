/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.BRACKET;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.KEYWORD;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.MULTIL_COMMENT;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.NUMBER;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.PAREN;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.STRING;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.URI;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XPATH_ATTRIBUTE;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XPATH_AXIS;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XPATH_FUNCTION;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XPATH_KIND_TEST;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.HighlightClasses.XPATH_PATH;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.SimpleRegexSyntaxHighlighter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathSyntaxHighlighter extends SimpleRegexSyntaxHighlighter {

    private static final String[] AXIS_NAMES = {
        "self", "child", "attribute", "descendant", "descendant-or-self", "ancestor",
        "ancestor-or-self", "following", "following-sibling", "namespace", "parent",
        "preceding-sibling",
    };

    private static final String[] KEYWORDS = {
        "or", "and", "not", "some", "in", "satisfies",
        "as", "is", "for", "every", "cast", "castable",
        "treat", "instance", "of", "to", "if", "then", "else",
        "return", "let",
        "intersect", "except", "union", "div", "idiv", "mod",
        "ne", "eq", "lt", "le", "gt", "ge",
    };

    private static final String[] KIND_TESTS = {
        "node", "document-node", "text", "comment",
        "namespace-node", "processing-instruction",
        "attribute", "schema-attribute", "element",
        "schema-element", "function",
    };


    private static final RegexHighlightGrammar GRAMMAR
        = grammarBuilder(XPATH_ATTRIBUTE.css, "@[\\w]+")
            .or(XPATH_PATH.css, "//?")
            .or(XPATH_AXIS.css, alternation(AXIS_NAMES) + "::")
            .or(KEYWORD.css, alternation(KEYWORDS))
            .or(XPATH_KIND_TEST.css, alternation(KIND_TESTS) + "\\(\\)")
            .or(XPATH_FUNCTION.css, "[\\w-]+?(?=\\()")
            .or(MULTIL_COMMENT.css, "\\(:.*?:\\)") // comments can be nested but whatever
            .or(PAREN.css, "[()]")
            .or(BRACKET.css, "[\\[\\]]")
            .or(NUMBER.css, "(\\.\\d++\\b|\\b\\d++\\.|(\\b\\d++(\\.\\d*+)?([eE][+-]?\\d+)?))")
            .or(STRING.css, "('([^']|'')*')|(\"([^\"]|\"\")*\")")
            .or(URI.css, "Q\\{[^{}]*}")
            .create();


    public XPathSyntaxHighlighter() {
        super("xpath", GRAMMAR);
    }


}
