/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

/**
 * Generic Antlr representation of a token.
 */
public class AntlrToken implements GenericToken<AntlrToken> {

    private final AntlrToken previousComment;
    private final TextDocument textDoc;
    private final String image;
    private final int endOffset;
    private final int startOffset;
    private final int channel;
    private final int kind;
    AntlrToken next;


    /**
     * Constructor
     *
     * @param token           The antlr token implementation
     * @param previousComment The previous comment
     * @param textDoc         The text document
     *
     * @deprecated Don't create antlr tokens directly, use an {@link AntlrTokenManager}
     */
    @Deprecated
    public AntlrToken(final Token token, final AntlrToken previousComment, TextDocument textDoc) {
        this.previousComment = previousComment;
        this.textDoc = textDoc;
        this.image = token.getText();
        this.startOffset = token.getStartIndex();
        this.endOffset = token.getStopIndex() + 1; // exclusive
        this.channel = token.getChannel();
        this.kind = token.getType();
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
        return image;
    }

    /** Returns a text region with the coordinates of this token. */
    @Override
    public TextRegion getRegion() {
        return TextRegion.fromBothOffsets(startOffset, endOffset);
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
    public int getKind() {
        return kind;
    }

    public boolean isHidden() {
        return !isDefault();
    }

    public boolean isDefault() {
        return channel == Lexer.DEFAULT_TOKEN_CHANNEL;
    }
}
