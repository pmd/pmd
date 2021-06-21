/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import java.io.IOException;

import net.sourceforge.pmd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

public abstract class JavaCCTokenizer implements Tokenizer {

    protected abstract TokenManager getLexerForSource(SourceCode sourceCode);

    protected TokenFilter getTokenFilter(TokenManager tokenManager) {
        return new JavaCCTokenFilter(tokenManager);
    }

    protected TokenEntry processToken(Tokens tokenEntries, GenericToken currentToken, String filename) {
        return new TokenEntry(currentToken.getImage(), filename, currentToken.getBeginLine(), currentToken.getBeginColumn(), currentToken.getEndColumn());
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException {
        TokenManager tokenManager = getLexerForSource(sourceCode);
        tokenManager.setFileName(sourceCode.getFileName());
        try {
            final TokenFilter tokenFilter = getTokenFilter(tokenManager);

            GenericToken currentToken = tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(processToken(tokenEntries, currentToken, sourceCode.getFileName()));
                currentToken = tokenFilter.getNextToken();
            }
        } catch (TokenMgrError e) {
            throw e.withFileName(sourceCode.getFileName());
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
