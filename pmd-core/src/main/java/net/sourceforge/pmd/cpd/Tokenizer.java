/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Tokenizes a source file into tokens consumable by CPD.
 */
public interface Tokenizer {

    /**
     * Tokenize the source code and record tokens using the provided token factory.
     */
    void tokenize(TextDocument document, TokenFactory tokens) throws IOException;

    /**
     * Wraps a call to {@link #tokenize(TextDocument, TokenFactory)} to properly
     * create and close the token factory.
     */
    static void tokenize(Tokenizer tokenizer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = Tokens.factoryForFile(textDocument, tokens)) {
            tokenizer.tokenize(textDocument, tf);
        }
    }
}
