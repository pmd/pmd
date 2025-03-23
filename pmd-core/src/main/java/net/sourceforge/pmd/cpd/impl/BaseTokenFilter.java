/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import static net.sourceforge.pmd.util.IteratorUtil.AbstractIterator;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * A generic filter for PMD token managers that allows to use comments
 * to enable / disable analysis of parts of the stream
 */
public class BaseTokenFilter<T extends GenericToken<T>> implements TokenManager<T> {

    private final TokenManager<T> tokenManager;
    private final LinkedList<T> unprocessedTokens; // NOPMD - used both as Queue and List
    private final Iterable<T> remainingTokens;
    private boolean discardingSuppressing;
    private T currentToken;

    /**
     * Creates a new BaseTokenFilter
     * @param tokenManager The token manager from which to retrieve tokens to be filtered
     */
    public BaseTokenFilter(final TokenManager<T> tokenManager) {
        this.tokenManager = tokenManager;
        this.unprocessedTokens = new LinkedList<>();
        this.remainingTokens = new RemainingTokens();
    }

    @Override
    public final T getNextToken() {
        currentToken = null;
        if (!unprocessedTokens.isEmpty()) {
            currentToken = unprocessedTokens.poll();
        } else {
            currentToken = tokenManager.getNextToken();
        }
        while (!shouldStopProcessing(currentToken)) {
            analyzeToken(currentToken);
            analyzeTokens(currentToken, remainingTokens);
            processCPDSuppression(currentToken);

            if (!isDiscarding()) {
                return currentToken;
            }

            if (!unprocessedTokens.isEmpty()) {
                currentToken = unprocessedTokens.poll();
            } else {
                currentToken = tokenManager.getNextToken();
            }
        }

        return null;
    }

    private boolean isDiscarding() {
        return discardingSuppressing || isLanguageSpecificDiscarding();
    }

    private void processCPDSuppression(final T currentToken) {
        // Check if a comment is altering the suppression state
        T comment = currentToken.getPreviousComment();
        while (comment != null) {
            if (comment.getImage().contains("CPD-OFF")) {
                discardingSuppressing = true;
                break;
            }
            if (comment.getImage().contains("CPD-ON")) {
                discardingSuppressing = false;
                break;
            }
            comment = comment.getPreviousComment();
        }
    }

    /**
     * Extension point for subclasses to analyze all tokens (before filtering)
     * and update internal status to decide on custom discard rules.
     *
     * @param currentToken The token to be analyzed
     * @see #isLanguageSpecificDiscarding()
     */
    protected void analyzeToken(final T currentToken) {
        // noop
    }

    /**
     * Extension point for subclasses to analyze all tokens (before filtering)
     * and update internal status to decide on custom discard rules.
     *
     * @param currentToken The token to be analyzed
     * @param remainingTokens All upcoming tokens
     * @see #isLanguageSpecificDiscarding()
     */
    protected void analyzeTokens(final T currentToken, final Iterable<T> remainingTokens) {
        // noop
    }

    /**
     * Extension point for subclasses to indicate tokens are to be filtered.
     *
     * @return True if tokens should be filtered, false otherwise
     */
    protected boolean isLanguageSpecificDiscarding() {
        return false;
    }

    /**
     * Extension point for subclasses to indicate when to stop filtering tokens.
     *
     * @param currentToken The token to be analyzed
     *
     * @return True if the token filter has finished consuming all tokens, false otherwise
     */
    protected boolean shouldStopProcessing(T currentToken) {
        return currentToken.isEof();
    }

    private final class RemainingTokens implements Iterable<T> {

        @Override
        public Iterator<T> iterator() {
            return new RemainingTokensIterator(currentToken);
        }

        private class RemainingTokensIterator extends AbstractIterator<T> implements Iterator<T> {

            int index = 0; // index of next element
            T startToken;

            RemainingTokensIterator(final T startToken) {
                this.startToken = startToken;
            }

            @Override
            protected void computeNext() {
                assert index >= 0;
                if (startToken != currentToken) { // NOPMD - intentional check for reference equality
                    throw new ConcurrentModificationException("Using iterator after next token has been requested.");
                }
                if (index < unprocessedTokens.size()) {
                    setNext(unprocessedTokens.get(index++));
                } else {
                    final T nextToken = tokenManager.getNextToken();
                    if (shouldStopProcessing(nextToken)) {
                        done();
                        return;
                    }
                    index++;
                    unprocessedTokens.add(nextToken);
                    setNext(nextToken);
                }
            }

        }
    }

}
