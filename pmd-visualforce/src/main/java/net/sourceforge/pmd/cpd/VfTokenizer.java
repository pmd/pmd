/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;
import net.sourceforge.pmd.lang.vf.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

/**
 * @author sergey.gorbaty
 *
 */
public class VfTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(VfLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();

        try (Reader reader = IOUtil.skipBOM(new StringReader(buffer.toString()))) {
            TokenManager tokenMgr = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                    .getTokenManager(sourceCode.getFileName(), reader);
            Token currentToken = (Token) tokenMgr.getNextToken();

            while (currentToken.image.length() > 0) {
                tokenEntries.add(new TokenEntry(String.valueOf(currentToken.kind), sourceCode.getFileName(),
                        currentToken.beginLine, currentToken.beginColumn, currentToken.endColumn));
                currentToken = (Token) tokenMgr.getNextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }
}
