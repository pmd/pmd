/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.matlab.MatlabTokenManager;
import net.sourceforge.pmd.lang.matlab.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The Matlab Tokenizer.
 */
public class MatlabTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        try (Reader reader = IOUtil.skipBOM(new StringReader(buffer.toString()))) {
            MatlabTokenManager tokenManager = new MatlabTokenManager(reader);
            tokenManager.setFileName(sourceCode.getFileName());
            final TokenFilter tokenFilter = new JavaCCTokenFilter(tokenManager);
            Token currentToken = (Token) tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(new TokenEntry(currentToken.image, sourceCode.getFileName(), currentToken.beginLine));
                currentToken = (Token) tokenFilter.getNextToken();
            }
        } catch (IOException err) {
            err.printStackTrace();
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
