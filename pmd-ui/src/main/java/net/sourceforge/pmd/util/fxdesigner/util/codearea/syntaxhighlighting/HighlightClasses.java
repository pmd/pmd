/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public enum HighlightClasses {
    MULTIL_COMMENT("multi-line-comment"),
    SINGLEL_COMMENT("single-line-comment"),
    PAREN("paren"),
    BRACE("brace"),
    BRACKET("bracket"),
    SEMICOLON("semicolon"),
    BOOLEAN("boolean"),
    STRING("string"),
    CHAR("char"),
    NULL("null"),
    NUMBER("number"),
    KEYWORD("keyword");

    /** Name of the css class. */
    public final String css;


    HighlightClasses(String css) {
        this.css = css;
    }
}
