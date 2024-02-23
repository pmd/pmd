/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.cpd;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

import scala.meta.tokens.Token;

/**
 * Adapts the scala.meta.tokens.Token so that it can be used with the generic BaseTokenFilter
 */
public class ScalaTokenAdapter implements GenericToken<ScalaTokenAdapter> {

    private final Token token;
    private final TextDocument textDocument;
    private final ScalaTokenAdapter previousComment;

    ScalaTokenAdapter(Token token, TextDocument textDocument, ScalaTokenAdapter comment) {
        this.token = token;
        this.textDocument = textDocument;
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
    public Chars getImageCs() {
        return textDocument.sliceTranslatedText(getRegion());
    }

    @Override
    public TextRegion getRegion() {
        return TextRegion.fromBothOffsets(token.pos().start(), token.pos().end());
    }

    @Override
    public FileLocation getReportLocation() {
        return textDocument.toLocation(getRegion());
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
