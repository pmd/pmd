/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.ast.GenericToken;

import scala.meta.tokens.Token;

/**
 * Adapts the scala.meta.tokens.Token so that it can be used with the generic BaseTokenFilter
 */
public class ScalaTokenAdapter implements GenericToken<ScalaTokenAdapter> {

    private Token token;
    private ScalaTokenAdapter previousComment;

    ScalaTokenAdapter(Token token, ScalaTokenAdapter comment) {
        this.token = token;
        this.previousComment = comment;
    }

    @Override
    public ScalaTokenAdapter getNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScalaTokenAdapter getPreviousComment() {
        return previousComment;
    }

    @Override
    public String getImage() {
        return token.text();
    }

    @Override
    public int getBeginLine() {
        return token.pos().startLine() + 1;
    }

    @Override
    public int getEndLine() {
        return token.pos().endLine() + 1;
    }

    @Override
    public int getBeginColumn() {
        return token.pos().startColumn() + 1;
    }

    @Override
    public int getEndColumn() {
        return token.pos().endColumn() + 2;
    }

    @Override
    public boolean isEof() {
        return token instanceof Token.EOF;
    }

    @Override
    public int getKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "ScalaTokenAdapter{"
                + "token=" + token
                + ", previousComment=" + previousComment
                + "}";
    }
}
