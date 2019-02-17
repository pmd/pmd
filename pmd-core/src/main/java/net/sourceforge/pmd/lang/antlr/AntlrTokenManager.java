/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.lang.TokenManager;

/**
 * Generic token manager implementation for all Antlr lexers.
 */
public class AntlrTokenManager implements TokenManager {
    private final Lexer lexer;
    private String fileName;
    private AntlrToken previousToken;

    /**
     * Constructor
     *
     * @param lexer The lexer
     * @param fileName The file name
     */
    public AntlrTokenManager(final Lexer lexer, final String fileName) {
        this.lexer = lexer;
        this.fileName = fileName;
        resetListeners();
    }

    @Override
    public Object getNextToken() {
        AntlrToken nextToken = getNextTokenFromAnyChannel();
        while (!nextToken.isDefault()) {
            nextToken = getNextTokenFromAnyChannel();
        }
        return nextToken;
    }

    private AntlrToken getNextTokenFromAnyChannel() {
        final AntlrToken previousComment = previousToken != null && previousToken.isHidden() ? previousToken : null;
        final AntlrToken currentToken = new AntlrToken(lexer.nextToken(), previousComment);
        previousToken = currentToken;

        return currentToken;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    private void resetListeners() {
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorHandler());
    }

    private static class ErrorHandler extends BaseErrorListener {

        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
                                final int charPositionInLine, final String msg, final RecognitionException ex) {
            throw new ANTLRSyntaxError(msg, line, charPositionInLine, ex);
        }
    }

    public static class ANTLRSyntaxError extends RuntimeException {
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
