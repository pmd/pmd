/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.jsp.ast.JspParserTokenManager;

/**
 * JSP Token Manager implementation.
 *
 * @deprecated This is internal API
 */
@Deprecated
@InternalApi
public class JspTokenManager implements TokenManager {
    private final JspParserTokenManager tokenManager;

    public JspTokenManager(Reader source) {
        tokenManager = new JspParserTokenManager(new JavaCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        JspParserTokenManager.setFileName(fileName);
    }
}
