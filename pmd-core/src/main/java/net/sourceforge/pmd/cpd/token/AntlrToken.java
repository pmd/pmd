/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * Generic Antlr representation of a token.
 */
public class AntlrToken implements GenericToken {

    private final Token token;
    private final AntlrToken previousComment;

    /**
     * Constructor
     *
     * @param token The antlr token implementation
     * @param previousComment The previous comment
     */
    public AntlrToken(final Token token, final AntlrToken previousComment) {
        this.token = token;
        this.previousComment = previousComment;
    }

    @Override
    public GenericToken getNext() {
        // Antlr implementation does not require this
        return null;
    }

    @Override
    public GenericToken getPreviousComment() {
        return previousComment;
    }

    @Override
    public String getImage() {
        return token.getText();
    }

    @Override
    public int getBeginLine() {
        return token.getLine();
    }

    @Override
    public int getEndLine() {
        return token.getLine();
    }

    @Override
    public int getBeginColumn() {
        return token.getCharPositionInLine();
    }

    @Override
    public int getEndColumn() {
        return token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex();
    }

    public int getKind() {
        return token.getType();
    }

    /**
     * @deprecated use {@link #getKind()} instead.
     */
    @Deprecated
    public int getType() {
        return getKind();
    }

    public boolean isHidden() {
        return token.getChannel() == Lexer.HIDDEN;
    }

    public boolean isDefault() {
        return token.getChannel() == Lexer.DEFAULT_TOKEN_CHANNEL;
    }
}
