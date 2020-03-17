/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.vf.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

/**
 * VF Token Manager implementation.
 *
 * @deprecated This is internal API, use {@link Parser#getTokenManager(String, Reader)} via
 *             {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@Deprecated
@InternalApi
public class VfTokenManager implements TokenManager {
    private final VfParserImplTokenManager tokenManager;

    public VfTokenManager(Reader source) {
        tokenManager = new VfParserImplTokenManager(CharStreamFactory.javaCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        VfParserImplTokenManager.setFileName(fileName);
    }
}
