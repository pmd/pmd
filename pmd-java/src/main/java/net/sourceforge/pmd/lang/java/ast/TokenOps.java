/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * PRIVATE FOR NOW, find out what is useful to move to the interface
 * (probably everything).
 *
 * @author Clément Fournier
 */
final class TokenOps {

    /**
     * Assumes no two tokens overlap, and that the two tokens are from
     * the same document.
     */
    private static final Comparator<GenericToken> TOKEN_POS_COMPARATOR
        = Comparator.comparing(GenericToken::getBeginLine).thenComparing(GenericToken::getBeginColumn);

    private TokenOps() {

    }

    public static int compare(GenericToken t1, GenericToken t2) {
        return TOKEN_POS_COMPARATOR.compare(t1, t2);
    }

    public static boolean isBefore(GenericToken t1, GenericToken t2) {
        return compare(t1, t2) < 0;
    }

    public static boolean isAfter(GenericToken t1, GenericToken t2) {
        return compare(t1, t2) > 0;
    }



    static GenericToken nthFollower(GenericToken token, int n) {
        while (n-- > 0 && token != null) {
            token = token.getNext();
        }
        if (token == null) {
            throw new NoSuchElementException("No such token");
        }

        return token;
    }

    /**
     * This is why we need to doubly link tokens... otherwise we need a
     * start hint.
     *
     * @param startHint Token from which to start iterating,
     *                  needed because tokens are not linked to their
     *                  previous token. Must be strictly before the anchor
     *                  and as close as possible as the expected position of
     *                  the anchor.
     * @param anchor    Anchor from which to apply the shift. The n-th previous
     *                  token will be returned
     * @param n         An int > 0
     *
     * @throws NoSuchElementException If there's less than n tokens to the left of the anchor.
     */
    // test only
    static GenericToken nthPrevious(GenericToken startHint, GenericToken anchor, int n) {
        if (compare(startHint, anchor) >= 0) {
            throw new IllegalStateException("Wrong left hint, possibly not left enough");
        }
        CircularBuffer<GenericToken> lookahead = new CircularBuffer<>(n);
        GenericToken current = startHint;
        while (current != null && !current.equals(anchor)) {
            lookahead.add(current);
            current = current.getNext();
        }
        if (!Objects.equals(current, anchor)) {
            throw new IllegalStateException("Wrong left hint, possibly not left enough");
        } else if (!lookahead.overflows()) {
            // not a full cycle, so we're not "n" tokens away from the anchor
            throw new NoSuchElementException("No such token");
        }

        return lookahead.getOldest();
    }


    /** A simple non-reusable FIFO circular buffer. */
    private static final class CircularBuffer<E> {

        private final Object[] contents;
        /** Index of the element to be overwritten on the next addition. */
        private int writeIdx;
        /** Total number of writes performed. */
        private int writeCount;


        private CircularBuffer(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("Buffer size can't be less than 1");
            }
            this.contents = new Object[size];
            this.writeIdx = 0;
        }

        private int shiftIdx() {
            int write = writeIdx;
            writeIdx = (writeIdx + 1) % contents.length;
            return write;
        }

        public void add(E elt) {
            contents[shiftIdx()] = elt;
            writeCount++;
        }

        /** Whether a full cycle has been completed at least once. */
        private boolean overflows() {
            return writeCount >= contents.length;
        }

        private int getOldestIdx() {
            return overflows() ? writeIdx : 0;
        }

        public E getOldest() {
            @SuppressWarnings("unchecked")
            E res = (E) contents[getOldestIdx()];
            return res;
        }
    }

}
