/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import java.io.IOException;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.CpdCompat;
import net.sourceforge.pmd.lang.document.TextDocument;

public abstract class JavaCCTokenizer implements Tokenizer {

    @SuppressWarnings("PMD.CloseResource")
    protected TokenManager<JavaccToken> getLexerForSource(TextDocument sourceCode) throws IOException {
        return makeLexerImpl(makeCharStream(sourceCode));
    }

    protected CharStream makeCharStream(TextDocument sourceCode) {
        return CharStreamFactory.simpleCharStream(sourceCode);
    }

    protected abstract TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode);

    protected TokenFilter<JavaccToken> getTokenFilter(TokenManager<JavaccToken> tokenManager) {
        return new JavaCCTokenFilter(tokenManager);
    }

    protected TokenEntry processToken(Tokens tokenEntries, JavaccToken currentToken) {
        return new TokenEntry(getImage(currentToken), currentToken.getReportLocation());
    }

    protected String getImage(JavaccToken token) {
        return token.getImage();
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException {
        try (TextDocument textDoc = TextDocument.create(CpdCompat.cpdCompat(sourceCode))) {
            TokenManager<JavaccToken> tokenManager = getLexerForSource(textDoc);
            final TokenFilter<JavaccToken> tokenFilter = getTokenFilter(tokenManager);
            JavaccToken currentToken = tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(processToken(tokenEntries, currentToken));
                currentToken = tokenFilter.getNextToken();
            }
        } catch (TokenMgrError e) {
            throw e.setFileName(sourceCode.getFileName());
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
