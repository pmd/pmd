/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript5.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * Ecmascript 5 Token Manager implementation.
 */
@InternalApi
public class Ecmascript5TokenManager implements TokenManager {
    private final Ecmascript5ParserImplTokenManager tokenManager;

    /**
     * Creates a new Ecmascript 5 Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public Ecmascript5TokenManager(Reader source) {
        tokenManager = new Ecmascript5ParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

}
