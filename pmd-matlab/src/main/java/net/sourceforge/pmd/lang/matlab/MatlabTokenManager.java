/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.matlab.ast.MatlabParserTokenManager;

/**
 * Matlab Token Manager implementation.
 *
 * @deprecated This is internal API
 */
@Deprecated
@InternalApi
public class MatlabTokenManager implements TokenManager {
    private final MatlabParserTokenManager tokenManager;

    /**
     * Creates a new Matlab Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public MatlabTokenManager(Reader source) {
        tokenManager = new MatlabParserTokenManager(new SimpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        MatlabParserTokenManager.setFileName(fileName);
    }
}
