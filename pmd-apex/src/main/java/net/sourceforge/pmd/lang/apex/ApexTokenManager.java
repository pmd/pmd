/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.python.ast.PythonParserTokenManager;

public class ApexTokenManager implements TokenManager {
    
	private final ApexParserTokenManager tokenManager;

    public ApexTokenManager(Reader source) {
        tokenManager = new ApexParserTokenManager(new SimpleCharStream(source));
    }

    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        ApexParserTokenManager.setFileName(fileName);
    }
}
