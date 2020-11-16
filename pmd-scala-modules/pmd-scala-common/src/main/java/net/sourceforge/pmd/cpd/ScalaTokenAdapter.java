/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.ast.GenericToken;

import scala.meta.tokens.Token;

/**
 * Adapts the scala.meta.tokens.Token so that it can be used with the generic BaseTokenFilter
 */
public class ScalaTokenAdapter implements GenericToken {

    private Token token;
    private GenericToken previousComment;

    ScalaTokenAdapter(Token token, GenericToken comment) {
        this.token = token;
        this.previousComment = comment;
    }

    @Override
    public GenericToken getNext() {
        // not required
        return null;
    }

    @Override
    public GenericToken getPreviousComment() {
        return previousComment;
    }

    @Override
    public String getImage() {
        return token.text();
    }

    @Override
    public int getBeginLine() {
        return token.pos().startLine();
    }

    @Override
    public int getEndLine() {
        return token.pos().endLine();
    }

    @Override
    public int getBeginColumn() {
        return token.pos().startColumn();
    }

    @Override
    public int getEndColumn() {
        return token.pos().endColumn();
    }
}
