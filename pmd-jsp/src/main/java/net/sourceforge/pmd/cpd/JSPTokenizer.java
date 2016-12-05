/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.jsp.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

public class JSPTokenizer implements Tokenizer {

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JspLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Reader reader = null;

        try {
            reader = new StringReader(buffer.toString());
            reader = IOUtil.skipBOM(reader);
            TokenManager tokenMgr = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                    .getTokenManager(sourceCode.getFileName(), reader);
            Token currentToken = (Token) tokenMgr.getNextToken();

            while (currentToken.image.length() > 0) {
                tokenEntries.add(new TokenEntry(String.valueOf(currentToken.kind), sourceCode.getFileName(),
                        currentToken.beginLine));
                currentToken = (Token) tokenMgr.getNextToken();
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        tokenEntries.add(TokenEntry.getEOF());
    }
}
