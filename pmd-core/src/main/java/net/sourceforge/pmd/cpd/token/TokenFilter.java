/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token;

import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * Defines filter to be applied to the token stream during CPD analysis
 */
public interface TokenFilter {

    /**
     * Retrieves the next token to pass the filter
     * @return The next token to pass the filter, or null if the end of the stream was reached
     */
    GenericToken getNextToken();
}
