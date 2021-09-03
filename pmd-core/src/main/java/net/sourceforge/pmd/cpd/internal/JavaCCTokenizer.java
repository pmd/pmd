/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.input.CharSequenceReader;

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
import net.sourceforge.pmd.util.IOUtil;

public abstract class JavaCCTokenizer implements Tokenizer {

    @SuppressWarnings("PMD.CloseResource")
    protected TokenManager<JavaccToken> getLexerForSource(SourceCode sourceCode) throws IOException {
        Reader reader = IOUtil.skipBOM(new CharSequenceReader(sourceCode.getCodeBuffer()));
        return makeLexerImpl(makeCharStream(reader));
    }

    protected CharStream makeCharStream(Reader sourceCode) throws IOException {
        return CharStreamFactory.simpleCharStream(sourceCode);
    }

    protected abstract TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode);

    protected TokenFilter<JavaccToken> getTokenFilter(TokenManager<JavaccToken> tokenManager) {
        return new JavaCCTokenFilter(tokenManager);
    }

    protected TokenEntry processToken(Tokens tokenEntries, JavaccToken currentToken, String filename) {
        return new TokenEntry(getImage(currentToken), filename, currentToken.getBeginLine(), currentToken.getBeginColumn(), currentToken.getEndColumn());
    }

    protected String getImage(JavaccToken token) {
        return token.getImage();
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException {
        TokenManager<JavaccToken> tokenManager = getLexerForSource(sourceCode);
        try {
            final TokenFilter<JavaccToken> tokenFilter = getTokenFilter(tokenManager);
            JavaccToken currentToken = tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(processToken(tokenEntries, currentToken, sourceCode.getFileName()));
                currentToken = tokenFilter.getNextToken();
            }
        } catch (TokenMgrError e) {
            throw e.setFileName(sourceCode.getFileName());
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
