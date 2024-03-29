/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;

/**
 * A generic filter for Antlr-based token managers that allows to use comments
 * to enable / disable analysis of parts of the stream
 */
public class AntlrTokenFilter extends BaseTokenFilter<AntlrToken> {

    /**
     * Creates a new AntlrTokenFilter
     * @param tokenManager The token manager from which to retrieve tokens to be filtered
     */
    public AntlrTokenFilter(final TokenManager<AntlrToken> tokenManager) {
        super(tokenManager);
    }

}
