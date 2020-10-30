/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import net.sourceforge.pmd.internal.util.IteratorUtil;
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
     * Returns the token's text.
     */
    default String getImage() {
        return getImageCs().toString();
    }

    /**
     * Returns the image as a {@link CharSequence}.
     */
    CharSequence getImageCs();


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


    /**
     * Returns an iterable that enumerates all special tokens belonging
     * to the given token.
     *
     * @param from Token from which to start, note that the returned iterable
     *             does not contain that token
     *
     * @return An iterator, possibly empty, not containing the parameter
     *
     * @throws NullPointerException If the parameter s null
     */
    static <T extends GenericToken<T>> Iterable<T> previousSpecials(T from) {
        return () -> IteratorUtil.generate(from.getPreviousComment(), GenericToken::getPreviousComment);
    }

}
