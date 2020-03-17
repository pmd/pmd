/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * Objective-C Token Manager implementation.
 */
@InternalApi
public final class ObjectiveCTokenManager implements TokenManager {
    private final ObjectiveCParserImplTokenManager tokenManager;

    /**
     * Creates a new Objective-C Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public ObjectiveCTokenManager(Reader source) {
        tokenManager = new ObjectiveCParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        ObjectiveCParserImplTokenManager.setFileName(fileName);
    }
}
