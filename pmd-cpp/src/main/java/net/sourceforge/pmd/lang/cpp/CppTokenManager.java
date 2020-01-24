/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.cpp.ast.CppParserTokenManager;

/**
 * C++ Token Manager implementation.
 */
public class CppTokenManager implements TokenManager {
    private final CppParserTokenManager tokenManager;

    /**
     * Creates a new C++ Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public CppTokenManager(Reader source) {
        tokenManager = new CppParserTokenManager(new CppCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        CppParserTokenManager.setFileName(fileName);
    }
}
