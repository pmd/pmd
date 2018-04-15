/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * A generic filter for JavaCC-based token managers that allows to use comments
 * to enable / disable analysis of parts of the stream
 */
public class JavaCCTokenFilter implements TokenFilter {

    private final TokenManager tokenManager;
    private boolean discardingSuppressing;
    
    /**
     * Creates a new JavaCCTokenFilter
     * @param tokenManager The token manager from which to retrieve tokens to be filtered
     */
    public JavaCCTokenFilter(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public final GenericToken getNextToken() {
        GenericToken currentToken = (GenericToken) tokenManager.getNextToken();
        while (!currentToken.getImage().isEmpty()) {
            analyzeToken(currentToken);
            processCPDSuppression(currentToken);

            if (!isDiscarding()) {
                return currentToken;
            }

            currentToken = (GenericToken) tokenManager.getNextToken();
        }
        
        return null;
    }

    private boolean isDiscarding() {
        return discardingSuppressing || isLanguageSpecificDiscarding();
    }

    private void processCPDSuppression(final GenericToken currentToken) {
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
     * Extension point for subclasses to indicate tokens are to be filtered.
     * 
     * @return True if tokens should be filtered, false otherwise
     */
    protected boolean isLanguageSpecificDiscarding() {
        return false;
    }
    
    /**
     * Extension point for subclasses to analyze all tokens (before filtering)
     * and update internal status to decide on custom discard rules.
     * 
     * @param currentToken The token to be analyzed
     * @see #isLanguageSpecificDiscarding()
     */
    protected void analyzeToken(final GenericToken currentToken) {
        // noop
    }

}
