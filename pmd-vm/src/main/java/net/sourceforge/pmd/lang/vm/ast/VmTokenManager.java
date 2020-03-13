/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;

public class VmTokenManager implements TokenManager {

    private final VmParserImplTokenManager vmParserTokenManager;

    public VmTokenManager(final Reader source) {
        vmParserTokenManager = new VmParserImplTokenManager(CharStreamFactory.javaCharStream(source));
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
