/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.TokenMgrError;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 */
public abstract class AntlrTokenizer implements Tokenizer {

    protected abstract Lexer getLexerForSource(final CharStream charStream);

    @Override
    public void tokenize(final SourceCode sourceCode, final Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();

        try {
            final CharStream charStream = CharStreams.fromString(buffer.toString());

            Lexer lexer = getLexerForSource(charStream);

            lexer.removeErrorListeners();
            lexer.addErrorListener(new ErrorHandler());

            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {
                if (token.getChannel() != Lexer.HIDDEN) {
                    final TokenEntry tokenEntry =
                            new TokenEntry(token.getText(), sourceCode.getFileName(), token.getLine());

                    tokenEntries.add(tokenEntry);
                }
                token = lexer.nextToken();
            }
        } catch (final ANTLRSyntaxError err) {
            // Wrap exceptions of the ANTLR tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError("Lexical error in file " + sourceCode.getFileName() + " at line " + err.getLine()
                    + ", column " + err.getColumn() + ".  Encountered: " + err.getMessage(),
                    TokenMgrError.LEXICAL_ERROR);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }

    private static class ErrorHandler extends BaseErrorListener {

        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
                                final int charPositionInLine, final String msg, final RecognitionException ex) {
            throw new ANTLRSyntaxError(msg, line, charPositionInLine, ex);
        }
    }

    private static class ANTLRSyntaxError extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final int line;
        private final int column;

        /* default */ ANTLRSyntaxError(final String msg, final int line, final int column,
                                       final RecognitionException cause) {
            super(msg, cause);
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
