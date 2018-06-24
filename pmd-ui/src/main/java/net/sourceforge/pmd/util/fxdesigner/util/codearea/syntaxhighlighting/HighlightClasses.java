/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public enum HighlightClasses {

    COMMENT(Constants.COMMENT),
    MULTIL_COMMENT("multi-line-comment", Constants.COMMENT),
    SINGLEL_COMMENT("single-line-comment", Constants.COMMENT),

    PUNCTUATION(Constants.PUNCTUATION),
    PAREN("paren", Constants.PUNCTUATION),
    BRACE("brace", Constants.PUNCTUATION),
    BRACKET("bracket", Constants.PUNCTUATION),
    SEMICOLON("semicolon", Constants.PUNCTUATION),

    LITERAL(Constants.LITERAL),
    BOOLEAN("boolean", Constants.LITERAL),
    STRING("string", Constants.LITERAL),
    URI("uri", "string", Constants.LITERAL),
    CHAR("char", Constants.LITERAL),
    NULL("null", Constants.LITERAL),
    NUMBER("number", Constants.LITERAL),

    KEYWORD("keyword"),
    ANNOTATION("annotation"),
    CLASS_IDENTIFIER("class-identifier"),
    
    // XPath specific
    XPATH_ATTRIBUTE("attribute", Constants.XPATH),
    XPATH_AXIS("axis", Constants.XPATH),
    XPATH_FUNCTION("function", Constants.XPATH),
    XPATH_PATH("path", Constants.XPATH, Constants.PUNCTUATION),
    XPATH_KIND_TEST("kind-test", "function", Constants.XPATH),

    XML_CDATA_TAG("cdata-tag", Constants.XML),
    XML_CDATA_CONTENT("cdata-content", Constants.XML),
    XML_PROLOG("xml-prolog", Constants.XML),
    XML_LT_GT("lt-gt", Constants.XML),
    XML_TAG_NAME("tag-name", Constants.XML),
    XML_ATTRIBUTE_NAME("attribute-name", Constants.XML);


    /** Name of the css class. */
    public final List<String> css;


    HighlightClasses(String... classes) {
        this.css = Collections.unmodifiableList(Arrays.asList(classes));
    }


    private static final class Constants {
        static final String LITERAL = "literal";
        static final String COMMENT = "comment";
        static final String PUNCTUATION = "punctuation";
        static final String XML = "xml";
        static final String XPATH = "xpath";
    }
}
