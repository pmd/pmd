/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5TokenKinds;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5TokenManager;
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
    protected String getImage(JavaccToken jsToken) {
        // Remove line continuation characters from string literals
        if (jsToken.kind == Ecmascript5TokenKinds.STRING_LITERAL
            || jsToken.kind == Ecmascript5TokenKinds.UNTERMINATED_STRING_LITERAL) {
            return jsToken.getImage().replaceAll("(?<!\\\\)\\\\(\\r\\n|\\r|\\n)", "");
        }
        return jsToken.getImage();
    }
}
