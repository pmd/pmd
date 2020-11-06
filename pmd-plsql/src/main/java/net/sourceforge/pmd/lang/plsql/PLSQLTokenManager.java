/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserTokenManager;

/**
 * PLSQL Token Manager implementation.
 *
 * @deprecated This is internal API, use {@link net.sourceforge.pmd.lang.Parser#getTokenManager(String, Reader)} via
 *             {@link net.sourceforge.pmd.lang.LanguageVersionHandler#getParser(ParserOptions)}.
 */
@Deprecated
@InternalApi
public class PLSQLTokenManager implements TokenManager {
    private final PLSQLParserTokenManager tokenManager;

    public PLSQLTokenManager(Reader source) {
        tokenManager = new PLSQLParserTokenManager(new SimpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        PLSQLParserTokenManager.setFileName(fileName);
    }
}
