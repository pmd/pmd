/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.Reportable;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * Represents a language-independent token such as constants, values language reserved keywords, or comments.
 */
public interface GenericToken<T extends GenericToken<T>> extends Comparable<T>, Reportable {

    /**
     * Obtain the next generic token according to the input stream which generated the instance of this token.
     *
     * @return the next generic token if it exists; null if it does not exist
     */
    T getNext();

    /**
     * Obtain a comment-type token which, according to the input stream which generated the instance of this token,
     * precedes this instance token and succeeds the previous generic token (if there is any).
     *
     * @return the comment-type token if it exists; null if it does not exist
     */
    T getPreviousComment();

    /**
     * Returns the token's text as a string.
     */
    default String getImage() {
        return getImageCs().toString();
    }


    /**
     * Returns the text of the token as a char sequence.
     * This should be preferred when you can use eg {@link StringUtils}
     * to do some processing, without having to create a string.
     */
    CharSequence getImageCs();


    /**
     * Returns true if the image of this token equals
     * the given charsequence. This does not create a
     * string.
     *
     * @param charSeq A character sequence
     */
    default boolean imageEquals(CharSequence charSeq) {
        CharSequence imageCs = getImageCs();
        if (imageCs instanceof Chars) {
            return ((Chars) imageCs).contentEquals(charSeq);
        }
        return StringUtils.equals(imageCs, charSeq);
    }


    /** Returns a text region with the coordinates of this token. */
    TextRegion getRegion();

    /**
     * Returns true if this token is an end-of-file token. This is the
     * last token of token sequences that have been fully lexed.
     */
    boolean isEof();


    /**
     * Returns true if this token is implicit, ie was inserted artificially
     * and has a zero-length image.
     */
    default boolean isImplicit() {
        return false;
    }


    /**
     * This must return true if this token comes before the other token.
     * If they start at the same index, then the smaller token comes before
     * the other.
     */
    @Override
    default int compareTo(T o) {
        return getRegion().compareTo(o.getRegion());
    }


    /**
     * Returns an iterator that enumerates all (non-special) tokens
     * between the two tokens (bounds included).
     *
     * @param from First token to yield (inclusive)
     * @param to   Last token to yield (inclusive)
     *
     * @return An iterator
     *
     * @throws IllegalArgumentException If the first token does not come before the other token
     */
    static <T extends GenericToken<T>> Iterator<T> range(T from, T to) {
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException(from + " must come before " + to);
        }
        return IteratorUtil.generate(from, t -> t == to ? null : t.getNext());
    }

}
