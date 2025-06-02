/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Tokenizes a source file into tokens consumable by CPD.
 *
 * <p>Note: This interface has been called Tokenizer in PMD 6.</p>
 */
public interface CpdLexer {

    /**
     * Tokenize the source code and record tokens using the provided token factory.
     */
    void tokenize(TextDocument document, TokenFactory tokens) throws IOException;

    /**
     * Wraps a call to {@link #tokenize(TextDocument, TokenFactory)} to properly
     * create and close the token factory.
     */
    static void tokenize(CpdLexer cpdLexer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = Tokens.factoryForFile(textDocument, tokens)) {
            cpdLexer.tokenize(textDocument, tf);
        }
    }

    static Tokens tokenize(CpdLexer cpdLexer, TextDocument textDocument) throws IOException {
        Tokens tokens = new Tokens();
        tokenize(cpdLexer, textDocument, tokens);
        return tokens;
    }
}
