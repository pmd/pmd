/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * A generic token implementation for JavaCC parsers. Will probably help
 * remove those duplicated implementations that all have the same name.
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
 *
 * <p>TODO replace duplicates over PMD.
 */
public class JavaccToken implements GenericToken {

    /**
     * Kind for implicit tokens. Negative because JavaCC only picks
     * positive numbers for token kinds.
     */
    static final int IMPLICIT_TOKEN = -1;

    /**
     * An integer that describes the kind of this token.  This numbering
     * system is determined by JavaCCParser, and a table of these numbers is
     * stored in the file ...Constants.java.
     */
    public final int kind;

    protected final JavaccTokenDocument document;
    private final CharSequence image;
    private final int startInclusive;
    private final int endExclusive;

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


    /** {@link #undefined()} */
    private JavaccToken() {
        this(":undefined:");
    }

    /**
     * @deprecated This is used by a few deprecated tests about comments,
     *     will be removed when they're updated.
     */
    @Deprecated
    public JavaccToken(String image) {
        this(-1, image, -1, -1, null);
    }

    /**
     * Builds of the specified kind.
     */
    public JavaccToken(int kind,
                       CharSequence image,
                       int startInclusive,
                       int endExclusive,
                       JavaccTokenDocument document) {
        assert startInclusive <= endExclusive : "Offsets should be correctly ordered";

        this.kind = kind;
        this.image = image;
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
        this.document = document;
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
    public String getImage() {
        return image.toString();
    }

    @Override
    public int getStartInDocument() {
        return startInclusive;
    }

    @Override
    public int getEndInDocument() {
        return endExclusive;
    }

    @Override
    public int getBeginLine() {
        return document == null ? -1 : document.lineNumberFromOffset(startInclusive);
    }

    @Override
    public int getEndLine() {
        return document == null ? -1 : document.lineNumberFromOffset(endExclusive - 1);
    }

    @Override
    public int getBeginColumn() {
        return document == null ? -1 : document.columnFromOffset(startInclusive);
    }

    @Override
    public int getEndColumn() {
        return document == null ? -1 : document.columnFromOffset(endExclusive - 1);
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
            this.startInclusive,
            charStream.getEndOffset(),
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
            this.startInclusive,
            this.endExclusive,
            this.document
        );
        tok.specialToken = this.specialToken;
        tok.next = this.next;
        return tok;
    }

    /**
     * This is a transient token which JavaCC uses to simplify its logic.
     * It will never be found in a parsed token chain.
     */
    public static JavaccToken undefined() {
        return new JavaccToken();
    }

    /**
     * Creates an implicit token, with zero length, that is linked to
     * the given token as its special predecessor.
     *
     * @param next Token before which to insert the new token
     *
     * @return The implicit token
     */
    public static JavaccToken implicitBefore(JavaccToken next) {

        JavaccToken implicit = newImplicit(next.getStartInDocument(), next.document);

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

    @NonNull
    public static JavaccToken newImplicit(int offset, JavaccTokenDocument document) {
        return new JavaccToken(IMPLICIT_TOKEN,
                               "",
                               offset,
                               offset,
                               document);
    }

}

