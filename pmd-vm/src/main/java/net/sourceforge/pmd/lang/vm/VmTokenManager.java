/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.vm.ast.VmParserTokenManager;
import net.sourceforge.pmd.lang.vm.util.VelocityCharStream;

/**
 *
 * @deprecated This is internal API, use {@link Parser#getTokenManager(String, Reader)} via
 *             {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@Deprecated
@InternalApi
public class VmTokenManager implements TokenManager {

    private final VmParserTokenManager vmParserTokenManager;

    public VmTokenManager(final Reader source) {
        vmParserTokenManager = new VmParserTokenManager(new VelocityCharStream(source, 1, 1));
    }

    @Override
    public Object getNextToken() {
        return vmParserTokenManager.getNextToken();
    }

    @Override
    public void setFileName(final String fileName) {
        AbstractTokenManager.setFileName(fileName);
    }

}
