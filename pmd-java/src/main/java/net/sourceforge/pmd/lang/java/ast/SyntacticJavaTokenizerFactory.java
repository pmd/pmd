/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Creates a tokenizer, that uses the syntactic grammar to provide context
 * for the tokenizer when reducing the input characters to tokens.
 *
 * @deprecated This implementation has been superseded. It is not necessary to parse Java code in order to tokenize it.
 */
@Deprecated
public final class SyntacticJavaTokenizerFactory {
    private SyntacticJavaTokenizerFactory() {
        // factory class
    }

    @Deprecated
    public static TokenManager<JavaccToken> createTokenizer(CharStream cs) {
        return JavaTokenKinds.newTokenManager(cs);
    }
}
