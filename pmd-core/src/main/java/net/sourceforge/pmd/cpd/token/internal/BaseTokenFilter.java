/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token.internal;

import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * A generic filter for PMD token managers that allows to use comments
 * to enable / disable analysis of parts of the stream
 */
public abstract class BaseTokenFilter<T extends GenericToken> implements TokenFilter {

    private final TokenManager tokenManager;
    private boolean discardingSuppressing;

    /**
     * Creates a new BaseTokenFilter
     * @param tokenManager The token manager from which to retrieve tokens to be filtered
     */
    public BaseTokenFilter(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public final T getNextToken() {
        T currentToken = (T) tokenManager.getNextToken();
        while (!shouldStopProcessing(currentToken)) {
            analyzeToken(currentToken);
            processCPDSuppression(currentToken);

            if (!isDiscarding()) {
                return currentToken;
            }

            currentToken = (T) tokenManager.getNextToken();
        }

        return null;
    }

    private boolean isDiscarding() {
        return discardingSuppressing || isLanguageSpecificDiscarding();
    }

    private void processCPDSuppression(final T currentToken) {
        // Check if a comment is altering the suppression state
        GenericToken comment = currentToken.getPreviousComment();
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
     * @return True if the token filter has finished consuming all tokens, false otherwise
     */
    protected abstract boolean shouldStopProcessing(T currentToken);

}
