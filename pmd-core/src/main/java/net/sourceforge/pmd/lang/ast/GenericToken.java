/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Represents a language-independent token such as constants, values language reserved keywords, or comments.
 */
public interface GenericToken<T extends GenericToken<T>> {

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


    // TODO these default implementations are here for compatibility because
    //  the functionality is only used in pmd-java for now, though it could
    //  be ported. I prefer doing this as changing all the GenericToken in
    //  pmd-java to JavaccToken


    /** Inclusive start offset in the source file text. */
    default int getStartInDocument() {
        return -1;
    }


    /** Exclusive end offset in the source file text. */
    default int getEndInDocument() {
        return -1;
    }


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
        if (from.getStartInDocument() > to.getStartInDocument()) {
            throw new IllegalArgumentException(
                from + " (at " + from.getStartInDocument()
                    + ") must come before " + to + " (at " + to.getStartInDocument() + ")"
            );
        }
        return IteratorUtil.generate(from, t -> t == to ? null : t.getNext());
    }


    /**
     * Returns an iterable that enumerates all special tokens belonging
     * to the given token.
     *
     * @param from Token from which to start
     *
     * @return An iterator
     *
     * @throws NullPointerException If the parameter s null
     */
    static Iterable<JavaccToken> previousSpecials(JavaccToken from) {
        return () -> IteratorUtil.generate(from.getPreviousComment(), JavaccToken::getPreviousComment);
    }

}
