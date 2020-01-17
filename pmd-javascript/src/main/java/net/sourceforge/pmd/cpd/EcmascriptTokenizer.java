/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ecmascript5.Ecmascript5TokenManager;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5ParserConstants;
import net.sourceforge.pmd.lang.ecmascript5.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The Ecmascript Tokenizer
 */
public class EcmascriptTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return new Ecmascript5TokenManager(IOUtil.skipBOM(new StringReader(buffer.toString())));
    }

    @Override
    protected TokenEntry processToken(Tokens tokenEntries, GenericToken currentToken, String filename) {
        return new TokenEntry(getTokenImage(currentToken), filename, currentToken.getBeginLine(),
                currentToken.getBeginColumn(), currentToken.getEndColumn());
    }

    private String getTokenImage(GenericToken token) {
        Token jsToken = (Token) token;
        // Remove line continuation characters from string literals
        if (jsToken.kind == Ecmascript5ParserConstants.STRING_LITERAL
                || jsToken.kind == Ecmascript5ParserConstants.UNTERMINATED_STRING_LITERAL) {
            return token.getImage().replaceAll("(?<!\\\\)\\\\(\\r\\n|\\r|\\n)", "");
        }
        return token.getImage();
    }
}
