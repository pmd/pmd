/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

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
     * A set of token images that are common in programs of this language.
     * These may be e.g. the images of keywords and punctuation of the language.
     * This is an optional method, if implemented, it allows reducing contention
     * on token image ID assignment. It is highly recommended to implement it though.
     * For instance in Java programs, two thirds of all tokens are either keywords
     * or punctuation or operators, whose image is known beforehand.
     */
    default Set<String> commonImages() {
        return Collections.emptySet();
    }


    /**
     * Wraps a call to {@link #tokenize(TextDocument, TokenFactory)} to properly
     * create and close the token factory.
     * @deprecated {@link Tokens} should be used at most in unit tests. This method is not publicly supported anymore.
     */
    @Deprecated
    static void tokenize(CpdLexer cpdLexer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = tokens.factoryForFile(textDocument)) {
            cpdLexer.tokenize(textDocument, tf);
        }
    }

    /**
     * @deprecated {@link Tokens} should be used at most in unit tests. This method is not publicly supported anymore.
     */
    @Deprecated
    static Tokens tokenize(CpdLexer cpdLexer, TextDocument textDocument) throws IOException {
        Tokens tokens = new Tokens();
        tokenize(cpdLexer, textDocument, tokens);
        return tokens;
    }
}
