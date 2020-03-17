/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript5;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5ParserTokenManager;

/**
 * Ecmascript 5 Token Manager implementation.
 *
 * @deprecated This is internal API
 */
@Deprecated
@InternalApi
public class Ecmascript5TokenManager implements TokenManager {
    private final Ecmascript5ParserTokenManager tokenManager;

    /**
     * Creates a new Ecmascript 5 Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public Ecmascript5TokenManager(Reader source) {
        tokenManager = new Ecmascript5ParserTokenManager(new SimpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        Ecmascript5ParserTokenManager.setFileName(fileName);
    }
}
