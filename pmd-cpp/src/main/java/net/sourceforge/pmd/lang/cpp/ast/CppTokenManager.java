/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;

/**
 * C++ Token Manager implementation.
 */
@InternalApi
public final class CppTokenManager implements TokenManager {
    private final CppParserImplTokenManager tokenManager;

    /**
     * Creates a new C++ Token Manager from the given source code.
     *
     * @param source the source code
     */
    public CppTokenManager(Reader source) {
        tokenManager = new CppParserImplTokenManager(CppCharStream.newCppCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        CppParserImplTokenManager.setFileName(fileName);
    }
}
