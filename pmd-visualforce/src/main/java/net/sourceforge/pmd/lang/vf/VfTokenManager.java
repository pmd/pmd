/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.vf.ast.VfParserTokenManager;

/**
 * VF Token Manager implementation.
 */
public class VfTokenManager implements TokenManager {
    private final VfParserTokenManager tokenManager;

    public VfTokenManager(Reader source) {
        tokenManager = new VfParserTokenManager(new JavaCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        VfParserTokenManager.setFileName(fileName);
    }
}
