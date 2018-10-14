/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 */
public abstract class AntlrTokenizer implements Tokenizer {

    protected abstract AntlrTokenManager getLexerForSource(SourceCode sourceCode);

    @Override
    public void tokenize(final SourceCode sourceCode, final Tokens tokenEntries) {

        AntlrTokenManager tokenManager = getLexerForSource(sourceCode);

        try {
            AntlrToken token = (AntlrToken) tokenManager.getNextToken();

            while (token.getType() != Token.EOF) {
                if (!token.isHidden()) {
                    final TokenEntry tokenEntry =
                            new TokenEntry(token.getImage(), tokenManager.getFileName(), token.getBeginLine());

                    tokenEntries.add(tokenEntry);
                }
                token = (AntlrToken) tokenManager.getNextToken();
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

    /* default */ static CharStream getCharStreamFromSourceCode(final SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return CharStreams.fromString(buffer.toString());
    }
}
