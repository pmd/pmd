/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import java.io.IOException;

import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.token.internal.BaseTokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.TextDocument;

public abstract class TokenizerBase<T extends GenericToken<T>> implements Tokenizer {

    protected abstract TokenManager<T> makeLexerImpl(TextDocument doc);

    protected TokenManager<T> filterTokenStream(TokenManager<T> tokenManager) {
        return new BaseTokenFilter<>(tokenManager);
    }

    protected void processToken(TokenFactory tokenEntries, T currentToken) {
       tokenEntries.recordToken(getImage(currentToken), currentToken.getReportLocation());
    }

    protected String getImage(T token) {
        return token.getImage();
    }

    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) throws IOException {
        TokenManager<T> tokenManager = filterTokenStream(makeLexerImpl(document));
        T currentToken = tokenManager.getNextToken();
        while (currentToken != null) {
            processToken(tokenEntries, currentToken);
            currentToken = tokenManager.getNextToken();
        }
    }
}