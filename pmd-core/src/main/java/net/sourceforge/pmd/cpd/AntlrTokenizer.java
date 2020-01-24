/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 *
 * @deprecated This is an internal API.
 */
@Deprecated
@InternalApi
public abstract class AntlrTokenizer implements Tokenizer {

    protected abstract AntlrTokenManager getLexerForSource(SourceCode sourceCode);

    @Override
    public void tokenize(final SourceCode sourceCode, final Tokens tokenEntries) {

        final AntlrTokenManager tokenManager = getLexerForSource(sourceCode);
        tokenManager.setFileName(sourceCode.getFileName());

        final AntlrTokenFilter tokenFilter = getTokenFilter(tokenManager);

        try {
            AntlrToken currentToken = tokenFilter.getNextToken();
            while (currentToken != null) {
                processToken(tokenEntries, tokenManager.getFileName(), currentToken);
                currentToken = tokenFilter.getNextToken();
            }
        } catch (final AntlrTokenManager.ANTLRSyntaxError err) {
            // Wrap exceptions of the ANTLR tokenizer in a TokenMgrError, so they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line option
            throw new TokenMgrError("Lexical error in file " + tokenManager.getFileName() + " at line "
                    + err.getLine() + ", column " + err.getColumn() + ".  Encountered: " + err.getMessage(),
                    TokenMgrError.LEXICAL_ERROR);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }

    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new AntlrTokenFilter(tokenManager);
    }

    /* default */ static CharStream getCharStreamFromSourceCode(final SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return CharStreams.fromString(buffer.toString());
    }

    private void processToken(final Tokens tokenEntries, final String fileName, final AntlrToken token) {
        final TokenEntry tokenEntry = new TokenEntry(token.getImage(), fileName, token.getBeginLine(), token.getBeginColumn() + 1, token.getEndColumn() + 1);
        tokenEntries.add(tokenEntry);
    }
}
