/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;

/**
 * Strategy to customize some aspects of the mapping
 * from Antlr tokens to PMD/CPD tokens.
 */
public class AntlrLexerBehavior {


    /**
     * Return the image that the token should have, possibly applying a transformation.
     * The default just returns {@link Token#getText()}.
     * Transformations here are usually normalizations, for instance, mapping
     * the image of all keywords to uppercase/lowercase to implement case-insensitivity,
     * or replacing the image of literals by a placeholder to implement {@link CpdLanguageProperties#CPD_ANONYMIZE_LITERALS}.
     *
     * @param token A token from the Antlr Lexer
     *
     * @return The image
     */
    protected String getTokenImage(Token token) {
        return token.getText();
    }
}
