/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;

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
 * class in a typical PMD run and this may reduce GC pressur.
 * </ul>
 *
 * <p>TODO replace duplicates over PMD.
 */
public class JavaccToken implements GenericToken, java.io.Serializable {

    /**
     * The version identifier for this Serializable class.
     * Increment only if the <i>serialized</i> form of the
     * class changes.
     */
    private static final long serialVersionUID = 4L;

    /**
     * An integer that describes the kind of this token.  This numbering
     * system is determined by JavaCCParser, and a table of these numbers is
     * stored in the file ...Constants.java.
     */
    public final int kind;
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

    private final CharSequence image;
    private final int startInclusive;
    private final int endExclusive;
    protected final TokenDocument document;

    /** {@link #undefined()} */
    private JavaccToken() {
        this(null);
    }

    public JavaccToken(String image) {
        this(-1, image, -1, -1, null);
    }

    /**
     * Constructs a new token for the specified Image and Kind.
     */
    public JavaccToken(int kind,
                       CharSequence image,
                       int startInclusive,
                       int endExclusive,
                       TokenDocument document) {
        this.kind = kind;
        this.image = image;
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
        this.document = document;
    }


    @Override
    public GenericToken getNext() {
        return next;
    }

    @Override
    public GenericToken getPreviousComment() {
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

    /**
     * Returns the image.
     */
    @Override
    public String toString() {
        return image.toString();
    }

    public static JavaccToken undefined() {
        return new JavaccToken();
    }

}

