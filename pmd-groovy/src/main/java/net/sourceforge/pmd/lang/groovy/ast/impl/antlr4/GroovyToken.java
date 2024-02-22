/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

import groovyjarjarantlr4.v4.runtime.Lexer;
import groovyjarjarantlr4.v4.runtime.Token;

/**
 * A Groovy specific token representation.
 * 
 * This is simply a copy of {@link AntlrToken} but
 * referencing the jarjared version of antlr4 used by the groovy lexer.
 */
public class GroovyToken implements GenericToken<GroovyToken> {

    private final Token token;
    private final GroovyToken previousComment;
    private final TextDocument textDoc;
    GroovyToken next;


    /**
     * Constructor
     *
     * @param token           The antlr token implementation
     * @param previousComment The previous comment
     * @param textDoc         The text document
     */
    public GroovyToken(final Token token, final GroovyToken previousComment, TextDocument textDoc) {
        this.token = token;
        this.previousComment = previousComment;
        this.textDoc = textDoc;
    }

    @Override
    public GroovyToken getNext() {
        return next;
    }

    @Override
    public GroovyToken getPreviousComment() {
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
    public int compareTo(GroovyToken o) {
        return getRegion().compareTo(o.getRegion());
    }

    @Override
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
