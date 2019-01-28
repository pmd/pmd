package net.sourceforge.pmd.cpd.token;

import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;

public class AntlrTokenFilter implements TokenFilter {

    private TokenFilter tokenFilter;

    public AntlrTokenFilter(final AntlrTokenManager tokenManager) {
        this.tokenFilter = new JavaCCTokenFilter(tokenManager);
    }

    @Override
    public AntlrToken getNextToken() {
        return (AntlrToken) tokenFilter.getNextToken();
    }
}
