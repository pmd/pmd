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
final class TokenUtils {

    // we use JavaccToken to avoid using getBeginLine and getEndLine,
    // which are now slower.

    /**
     * Assumes no two tokens overlap, and that the two tokens are from
     * the same document.
     */
    private static final Comparator<GenericToken> TOKEN_POS_COMPARATOR
        = Comparator.comparingInt(GenericToken::getStartInDocument);

    private TokenUtils() {

    }

    public static int compare(GenericToken t1, GenericToken t2) {
        return TOKEN_POS_COMPARATOR.compare(t1, t2);
    }

    public static boolean isBefore(GenericToken t1, GenericToken t2) {
        return t1.getStartInDocument() < t2.getStartInDocument();
    }

    public static boolean isAfter(GenericToken t1, GenericToken t2) {
        return t1.getStartInDocument() > t2.getStartInDocument();

    }


    public static GenericToken nthFollower(GenericToken token, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative index?");
        }
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
     *                  and as close as possible to the expected position of
     *                  the anchor.
     * @param anchor    Anchor from which to apply the shift. The n-th previous
     *                  token will be returned
     * @param n         An int > 0
     *
     * @throws NoSuchElementException If there's less than n tokens to the left of the anchor.
     */
    // test only
    public static GenericToken nthPrevious(GenericToken startHint, GenericToken anchor, int n) {
        if (compare(startHint, anchor) >= 0) {
            throw new IllegalStateException("Wrong left hint, possibly not left enough");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Offset can't be less than 1");
        }
        int numAway = 0;
        GenericToken target = startHint;
        GenericToken current = startHint;
        while (current != null && !current.equals(anchor)) {
            current = current.getNext();
            // wait "n" iterations before starting to advance the target
            // then advance "target" at the same rate as "current", but
            // "n" tokens to the left
            if (numAway == n) {
                target = target.getNext();
            } else {
                numAway++;
            }
        }
        if (!Objects.equals(current, anchor)) {
            throw new IllegalStateException("Wrong left hint, possibly not left enough");
        } else if (numAway != n) {
            // We're not "n" tokens away from the anchor
            throw new NoSuchElementException("No such token");
        }

        return target;
    }
}
