/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token;

import net.sourceforge.pmd.cpd.token.internal.BaseTokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * A generic filter for JavaCC-based token managers that allows to use comments
 * to enable / disable analysis of parts of the stream
 */
public class JavaCCTokenFilter extends BaseTokenFilter<GenericToken> {

    /**
     * Creates a new JavaCCTokenFilter
     * @param tokenManager The token manager from which to retrieve tokens to be filtered
     */
    public JavaCCTokenFilter(final TokenManager tokenManager) {
        super(tokenManager);
    }

    @Override
    protected boolean shouldStopProcessing(final GenericToken currentToken) {
        return currentToken.getImage().isEmpty();
    }
}
