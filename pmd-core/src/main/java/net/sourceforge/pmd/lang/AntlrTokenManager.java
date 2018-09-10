/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.token.GenericAntlrToken;

import com.beust.jcommander.internal.Nullable;

/**
 * Generic token manager implementation for all Antlr lexers.
 */
public class AntlrTokenManager implements TokenManager {
    private final Lexer lexer;
    private String fileName;
    private final String commentToken;
    private GenericAntlrToken previousComment;

    /**
     * Constructor
     *
     * @param lexer The lexer
     * @param fileName The file name
     * @param commentToken The list of all comment tokens on the grammar
     */
    public AntlrTokenManager(final Lexer lexer, final String fileName, @Nullable final String commentToken) {
        this.lexer = lexer;
        this.fileName = fileName;
        this.commentToken = commentToken;
    }

    @Override
    public Object getNextToken() {
        final Token token = lexer.nextToken();
        if (isCommentToken(token.getText())) {
            previousComment = new GenericAntlrToken(token, previousComment);
        }

        return new GenericAntlrToken(token, previousComment);
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void resetListeners() {
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorHandler());
    }

    private boolean isCommentToken(final String text) {
        return commentToken != null && text != null && text.startsWith(commentToken);
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
