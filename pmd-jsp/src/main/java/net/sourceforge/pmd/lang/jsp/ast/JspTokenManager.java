/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * JSP Token Manager implementation.
 */
public class JspTokenManager implements TokenManager {
    private final JspParserImplTokenManager tokenManager;

    public JspTokenManager(Reader source) {
        tokenManager = new JspParserImplTokenManager(CharStreamFactory.javaCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        JspParserImplTokenManager.setFileName(fileName);
    }
}
