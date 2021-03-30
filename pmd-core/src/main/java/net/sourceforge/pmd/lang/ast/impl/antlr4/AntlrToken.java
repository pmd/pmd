/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * Generic Antlr representation of a token.
 */
public class AntlrToken implements GenericToken<AntlrToken> {

    private final Token token;
    private final AntlrToken previousComment;
    private final TextDocument textDoc;
    AntlrToken next;


    /**
     * Constructor
     *
     * @param token           The antlr token implementation
     * @param previousComment The previous comment
     * @param textDoc         The text document
     */
    public AntlrToken(final Token token, final AntlrToken previousComment, TextDocument textDoc) {
        this.token = token;
        this.previousComment = previousComment;
        this.textDoc = textDoc;
    }

    @Override
    public AntlrToken getNext() {
        return next;
    }

    @Override
    public AntlrToken getPreviousComment() {
        return previousComment;
    }

    @Override
    public CharSequence getImageCs() {
        return token.getText();
    }

    /** Returns a text region with the coordinates of this token. */
    @Override
    public TextRegion getRegion() {
        return TextRegion.fromBothOffsets(token.getStartIndex(), token.getStopIndex() + 1);
    }

    @Override
    public FileLocation getReportLocation() {
        return textDoc.toLocation(getRegion());
    }

    @Override
    public boolean isEof() {
        return getKind() == Token.EOF;
    }

    @Override
    public int compareTo(AntlrToken o) {
        return getRegion().compareTo(o.getRegion());
    }

    @Override
    @Experimental
    public int getKind() {
        return token.getType();
    }

    public boolean isHidden() {
        return !isDefault();
    }

    public boolean isDefault() {
        return token.getChannel() == Lexer.DEFAULT_TOKEN_CHANNEL;
    }
}
