/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.objectivec.ObjectiveCTokenManager;
import net.sourceforge.pmd.lang.objectivec.ast.Token;

/**
 * The Objective-C Tokenizer
 */
public class ObjectiveCTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        try (Reader reader = new StringReader(buffer.toString())) {
            ObjectiveCTokenManager tokenManager = new ObjectiveCTokenManager(reader);
            tokenManager.setFileName(sourceCode.getFileName());
            final TokenFilter tokenFilter = new JavaCCTokenFilter(tokenManager);
            Token currentToken = (Token) tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(new TokenEntry(currentToken.image, sourceCode.getFileName(), currentToken.beginLine));
                currentToken = (Token) tokenFilter.getNextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
