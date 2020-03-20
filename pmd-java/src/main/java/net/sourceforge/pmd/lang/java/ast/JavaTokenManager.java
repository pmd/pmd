/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Java Token Manager implementation.
 */
public class JavaTokenManager implements TokenManager<JavaccToken> {
    private final JavaParserImplTokenManager tokenManager;

    public JavaTokenManager(Reader source) {
        tokenManager = new JavaParserImplTokenManager(CharStreamFactory.javaCharStream(source));
    }

    @Override
    public JavaccToken getNextToken() {
        return tokenManager.getNextToken();
    }

}
