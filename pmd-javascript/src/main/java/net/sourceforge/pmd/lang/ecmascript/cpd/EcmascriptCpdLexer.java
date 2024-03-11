/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.cpd;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5TokenKinds;

/**
 * The Ecmascript Tokenizer
 *
 * <p>Note: This class has been called EcmascriptTokenizer in PMD 6</p>.
 */
public class EcmascriptCpdLexer extends JavaccCpdLexer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return Ecmascript5TokenKinds.newTokenManager(CharStream.create(doc));
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
