/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.cpp;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.cpp.ast.CppParserTokenManager;

/**
 * C++ Token Manager implementation.
 */
public class CppTokenManager implements TokenManager {
    private final CppParserTokenManager tokenManager;

    public CppTokenManager(Reader source) {
	tokenManager = new CppParserTokenManager(new SimpleCharStream(new ContinuationReader(source)));
    }

    public Object getNextToken() {
	return tokenManager.getNextToken();
    }

    public void setFileName(String fileName) {
	tokenManager.setFileName(fileName);
    }
}
