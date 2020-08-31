/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Generic token manager implementation for all Antlr lexers.
 */
public class AntlrTokenManager implements TokenManager<AntlrToken> {

    private final Lexer lexer;
    private final TextDocument textDoc;
    private AntlrToken previousToken;


    public AntlrTokenManager(final Lexer lexer, final TextDocument textDocument) {
        this.lexer = lexer;
        this.textDoc = textDocument;
        resetListeners();
    }

    @Override
    public AntlrToken getNextToken() {
        AntlrToken nextToken = getNextTokenFromAnyChannel();
        while (!nextToken.isDefault()) {
            nextToken = getNextTokenFromAnyChannel();
        }
        return nextToken;
    }

    private AntlrToken getNextTokenFromAnyChannel() {
        final AntlrToken previousComment = previousToken != null && previousToken.isHidden() ? previousToken : null;
        final AntlrToken currentToken = new AntlrToken(lexer.nextToken(), previousComment, textDoc);
        if (previousToken != null) {
            previousToken.next = currentToken;
        }
        previousToken = currentToken;

        return currentToken;
    }

    public String getFileName() {
        return textDoc.getDisplayName();
    }

    private void resetListeners() {
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorHandler());
    }

    private class ErrorHandler extends BaseErrorListener {

        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer,
                                final Object offendingSymbol,
                                final int line,
                                final int charPositionInLine,
                                final String msg,
                                final RecognitionException ex) {
            throw new TokenMgrError(line, charPositionInLine, getFileName(), msg, ex);
        }
    }

}
