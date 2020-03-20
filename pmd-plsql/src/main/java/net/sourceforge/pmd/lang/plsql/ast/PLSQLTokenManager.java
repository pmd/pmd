/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * PLSQL Token Manager implementation.
 */
@InternalApi
public class PLSQLTokenManager implements TokenManager {
    private final PLSQLParserImplTokenManager tokenManager;

    public PLSQLTokenManager(Reader source) {
        tokenManager = new PLSQLParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        PLSQLParserImplTokenManager.setFileName(fileName);
    }
}
