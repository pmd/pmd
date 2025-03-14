/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.ast.impl.antlr4;

import org.apache.groovy.parser.antlr4.GroovyLexer;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;

import groovyjarjarantlr4.v4.runtime.ANTLRErrorListener;
import groovyjarjarantlr4.v4.runtime.Lexer;
import groovyjarjarantlr4.v4.runtime.RecognitionException;
import groovyjarjarantlr4.v4.runtime.Recognizer;

/**
 * A Groovy specific token manager.
 * 
 * This is simply a copy of {@link AntlrTokenManager} but
 * referencing the jarjared version of antlr4 used by the groovy lexer.
 */
public class GroovyTokenManager implements TokenManager<GroovyToken> {

    private final Lexer lexer;
    private final TextDocument textDoc;
    private GroovyToken previousToken;


    public GroovyTokenManager(final Lexer lexer, final TextDocument textDocument) {
        this.lexer = lexer;
        this.textDoc = textDocument;
        resetListeners();
    }

    @Override
    public GroovyToken getNextToken() {
        GroovyToken nextToken = getNextTokenFromAnyChannel();
        while (!nextToken.isDefault()) {
            nextToken = getNextTokenFromAnyChannel();
        }
        return nextToken;
    }

    private GroovyToken getNextTokenFromAnyChannel() {
        /*
         * Groovy's grammar doesn't hide away comments in a separate channel,
         * but includes them as NL tokens with a different image
         * See: https://github.com/apache/groovy/blob/GROOVY_4_0_15/src/antlr/GroovyLexer.g4#L980-L988
         */
        final GroovyToken previousComment;
        if (previousToken != null && previousToken.getKind() == GroovyLexer.NL
                && !"\n".equals(previousToken.getImage())) {
            previousComment = previousToken;
        } else {
            previousComment = null;
        }
        
        final GroovyToken currentToken = new GroovyToken(lexer.nextToken(), previousComment, textDoc);
        if (previousToken != null) {
            previousToken.next = currentToken;
        }
        previousToken = currentToken;

        return currentToken;
    }

    private void resetListeners() {
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorHandler());
    }

    private final class ErrorHandler implements ANTLRErrorListener<Object> {

        @Override
        public void syntaxError(final Recognizer recognizer,
                                final Object offendingSymbol,
                                final int line,
                                final int charPositionInLine,
                                final String msg,
                                final RecognitionException ex) {
            throw new LexException(line, charPositionInLine, textDoc.getFileId(), msg, ex);
        }
    }

}
