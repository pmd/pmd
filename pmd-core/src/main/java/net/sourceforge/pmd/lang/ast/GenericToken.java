/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * Represents a language-independent token such as constants, values language reserved keywords, or comments.
 */
public interface GenericToken<T extends GenericToken<T>> extends Comparable<T> {

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
    String getImage();


    /**
     * Returns true if this token is an end-of-file token. This is the
     * last token of token sequences that have been fully lexed.
     */
    boolean isEof();

    /**
     * Returns a region with the coordinates of this token.
     */
    TextRegion getRegion();

    // TODO remove those methods, instead, implement Reportable.
    //  This is already done for JavaccToken, to do for AntlrToken

    /**
     * Gets the line where the token's region begins
     *
     * @return a non-negative integer containing the begin line
     */
    int getBeginLine();


    /**
     * Gets the line where the token's region ends
     *
     * @return a non-negative integer containing the end line
     */
    int getEndLine();


    /**
     * Gets the column offset from the start of the begin line where the token's region begins
     *
     * @return a non-negative integer containing the begin column
     */
    int getBeginColumn();


    /**
     * Gets the column offset from the start of the end line where the token's region ends
     *
     * @return a non-negative integer containing the begin column
     */
    int getEndColumn();

    /**
     * Returns true if this token is implicit, ie was inserted artificially
     * and has a zero-length image.
     */
    default boolean isImplicit() {
        return false;
    }


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
    static Iterator<JavaccToken> range(JavaccToken from, JavaccToken to) {
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException(
                from + " (at " + from.getRegion() + ") must come before "
                    + to + " (at " + to.getRegion() + ")"
            );
        }
        return IteratorUtil.generate(from, t -> t == to ? null : t.getNext());
    }

}
