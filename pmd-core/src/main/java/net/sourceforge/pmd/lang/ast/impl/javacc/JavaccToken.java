/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * A generic token implementation for JavaCC parsers.
 *
 * <p>Largely has the same interface as the default generated token class.
 * The main difference is that the position of the token is encoded as
 * a start and end offset in the source file, instead of a (begin,end)x(line,column)
 * 4-tuple. This offers two practical advantages:
 * <ul>
 * <li>It allows retrieving easily the underlying text of a node (just
 * need to cut a substring of the file text). Other attributes like lines
 * and column bounds can be derived as well - though this should not be
 * done systematically because it's costlier.
 * <li>It's a bit lighter. Token instances are one of the most numerous
 * class in a typical PMD run and this may reduce GC pressure.
 * </ul>
 */
public class JavaccToken implements GenericToken<JavaccToken> {

    /**
     * Kind for EOF tokens.
     */
    public static final int EOF = 0;

    /**
     * Kind for implicit tokens. Negative because JavaCC only picks
     * positive numbers for token kinds.
     */
    public static final int IMPLICIT_TOKEN = -1;


    /**
     * An integer that describes the kind of this token.  This numbering
     * system is determined by JavaCCParser, and a table of these numbers is
     * stored in the file ...Constants.java.
     */
    public final int kind;

    private final JavaccTokenDocument document;
    private final CharSequence image;
    private final int startOffset;
    private final int endOffset;

    /**
     * A reference to the next regular (non-special) token from the input
     * stream.  If this is the last token from the input stream, or if the
     * token manager has not read tokens beyond this one, this field is
     * set to null.  This is true only if this token is also a regular
     * token.  Otherwise, see below for a description of the contents of
     * this field.
     */
    public JavaccToken next;

    /**
     * This field is used to access special tokens that occur prior to this
     * token, but after the immediately preceding regular (non-special) token.
     * If there are no such special tokens, this field is set to null.
     * When there are more than one such special token, this field refers
     * to the last of these special tokens, which in turn refers to the next
     * previous special token through its specialToken field, and so on
     * until the first special token (whose specialToken field is null).
     * The next fields of special tokens refer to other special tokens that
     * immediately follow it (without an intervening regular token).  If there
     * is no such token, this field is null.
     */
    public JavaccToken specialToken;


    /**
     * Builds a new token of the specified kind.
     *
     * @param kind           Kind of token
     * @param image          Image of the token (after translating escapes if any)
     * @param startInclusive Start character of the token in the text file (before translating escapes)
     * @param endExclusive   End of the token in the text file (before translating escapes)
     * @param document       Document owning the token
     */
    public JavaccToken(int kind,
                       CharSequence image,
                       int startInclusive,
                       int endExclusive,
                       JavaccTokenDocument document) {
        assert document != null : "Null document";
        assert TextRegion.isValidRegion(startInclusive, endExclusive, document.getTextDocument());

        this.kind = kind;
        this.image = image;
        this.startOffset = startInclusive;
        this.endOffset = endExclusive;
        this.document = document;
    }

    /**
     * Returns the document owning this token.
     */
    public final JavaccTokenDocument getDocument() {
        return document;
    }

    @Override
    public boolean isEof() {
        return kind == EOF;
    }

    @Override
    public JavaccToken getNext() {
        return next;
    }

    @Override
    public JavaccToken getPreviousComment() {
        return specialToken;
    }

    @Override
    public CharSequence getImageCs() {
        return image;
    }

    @Override
    public TextRegion getRegion() {
        return TextRegion.fromBothOffsets(startOffset, endOffset);
    }

    int getStartOffset() {
        return startOffset;
    }

    int getEndOffset() {
        return endOffset;
    }

    @Override
    public FileLocation getReportLocation() {
        return document.getTextDocument().toLocation(getRegion());
    }

    @Override
    public boolean isImplicit() {
        return kind == IMPLICIT_TOKEN;
    }

    @Override
    public String toString() {
        return document.describeKind(kind) + ": " + getImage();
    }

    /**
     * Returns a new token with the same kind as this one, whose image
     * is replaced by the one marked on the char stream.
     *
     * @param charStream Char stream from which to start
     *
     * @return A new token
     */
    public JavaccToken replaceImage(CharStream charStream) {
        return new JavaccToken(
            this.kind,
            charStream.GetImage(),
            this.startOffset,
            charStream.getEndOffset(),
            this.document
        );
    }

    public JavaccToken withImage(String image) {
        return new JavaccToken(
            this.kind,
            image,
            this.startOffset,
            this.endOffset,
            this.document
        );
    }



    /**
     * Returns a new token with the given kind, and all other parameters
     * identical to this one.
     *
     * @param newKind Char stream from which to start
     *
     * @return A new token
     */
    public JavaccToken withKind(int newKind) {
        JavaccToken tok = new JavaccToken(
            newKind,
            this.image,
            this.startOffset,
            this.endOffset,
            this.document
        );
        tok.specialToken = this.specialToken;
        tok.next = this.next;
        return tok;
    }

    /**
     * Creates an implicit token, with zero length, that is linked to
     * the given token as its special predecessor.
     *
     * @param next Token before which to insert the new token
     *
     * @return A new token
     */
    public static JavaccToken implicitBefore(JavaccToken next) {

        JavaccToken implicit = newImplicit(next.getRegion().getStartOffset(), next.document);

        // insert it right before the next token
        // as a special token
        implicit.next = next;

        if (next.specialToken != null) {
            next.specialToken.next = implicit;
            implicit.specialToken = next.specialToken;
        }

        next.specialToken = implicit;

        return implicit;
    }

    /**
     * Returns a new implicit token, positioned at the given offset.
     *
     * @param offset   Offset of the token
     * @param document Document owning the token
     *
     * @return A new token
     */
    public static JavaccToken newImplicit(int offset, JavaccTokenDocument document) {
        return new JavaccToken(IMPLICIT_TOKEN, "", offset, offset, document);
    }
}

