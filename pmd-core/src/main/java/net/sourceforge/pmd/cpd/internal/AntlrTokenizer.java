/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 */
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
            throw new TokenMgrError(err.getLine(), err.getColumn(), tokenManager.getFileName(), err.getMessage(), null);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }

    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new AntlrTokenFilter(tokenManager);
    }

    public static CharStream getCharStreamFromSourceCode(final SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return CharStreams.fromString(buffer.toString());
    }

    private void processToken(final Tokens tokenEntries, final String fileName, final AntlrToken token) {
        final TokenEntry tokenEntry = new TokenEntry(token.getImage(), fileName, token.getBeginLine());
        tokenEntries.add(tokenEntry);
    }
}
