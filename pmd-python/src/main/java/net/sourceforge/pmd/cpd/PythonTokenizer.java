/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
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
        try (Reader reader = IOUtil.skipBOM(new StringReader(buffer.toString()))) {
            LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(PythonLanguageModule.NAME)
                    .getDefaultVersion().getLanguageVersionHandler();
            TokenFilter tokenFilter = new JavaCCTokenFilter(languageVersionHandler
                    .getParser(languageVersionHandler.getDefaultParserOptions())
                    .getTokenManager(sourceCode.getFileName(), reader));
            Token currentToken = (Token) tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(new TokenEntry(currentToken.image, sourceCode.getFileName(), currentToken.beginLine));
                currentToken = (Token) tokenFilter.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.err.println("Added " + sourceCode);
        } catch (TokenMgrError | IOException err) {
            err.printStackTrace();
            System.err.println("Skipping " + sourceCode + " due to parse error");
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
