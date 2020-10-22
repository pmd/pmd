/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5TokenKinds;

/**
 * The Ecmascript Tokenizer
 */
public class EcmascriptTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return Ecmascript5TokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected String getImage(JavaccToken jsToken) {
        // Remove line continuation characters from string literals
        if (jsToken.kind == Ecmascript5TokenKinds.STRING_LITERAL
            || jsToken.kind == Ecmascript5TokenKinds.UNTERMINATED_STRING_LITERAL) {
            return jsToken.getImage().replaceAll("(?<!\\\\)\\\\(\\r\\n|\\r|\\n)", "");
        }
        return jsToken.getImage();
    }
}
