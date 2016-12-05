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
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.python.PythonLanguageModule;
import net.sourceforge.pmd.lang.python.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The Python tokenizer.
 */
public class PythonTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        Reader reader = null;
        try {
            LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(PythonLanguageModule.NAME)
                    .getDefaultVersion().getLanguageVersionHandler();
            reader = new StringReader(buffer.toString());
            reader = IOUtil.skipBOM(reader);
            TokenManager tokenManager = languageVersionHandler
                    .getParser(languageVersionHandler.getDefaultParserOptions())
                    .getTokenManager(sourceCode.getFileName(), reader);
            Token currentToken = (Token) tokenManager.getNextToken();
            while (currentToken.image.length() > 0) {
                tokenEntries.add(new TokenEntry(currentToken.image, sourceCode.getFileName(), currentToken.beginLine));
                currentToken = (Token) tokenManager.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.err.println("Added " + sourceCode);
        } catch (TokenMgrError err) {
            err.printStackTrace();
            System.err.println("Skipping " + sourceCode + " due to parse error");
            tokenEntries.add(TokenEntry.getEOF());
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
