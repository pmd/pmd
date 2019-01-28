package net.sourceforge.pmd.cpd.token;

import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;

public class AntlrHiddenTokenFilter extends AntlrTokenFilter {

    public AntlrHiddenTokenFilter(final AntlrTokenManager tokenManager) {
        super(tokenManager);
    }

    @Override
    public AntlrToken getNextToken() {
        AntlrToken token = super.getNextToken();
        return token.isHidden() ? getNextToken() : token;
    }
}
