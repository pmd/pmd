/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * Java Token Manager implementation.
 */
public class JavaTokenManager implements TokenManager {
    private final JavaParserImplTokenManager tokenManager;

    public JavaTokenManager(Reader source) {
        tokenManager = new JavaParserImplTokenManager(CharStreamFactory.javaCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        tokenManager.setFileName(fileName);
    }
}
