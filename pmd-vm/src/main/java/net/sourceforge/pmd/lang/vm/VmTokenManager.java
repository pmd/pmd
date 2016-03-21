/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.vm.ast.VmParserTokenManager;
import net.sourceforge.pmd.lang.vm.util.VelocityCharStream;

public class VmTokenManager implements TokenManager {

    private final VmParserTokenManager vmParserTokenManager;

    public VmTokenManager(final Reader source) {
        vmParserTokenManager = new VmParserTokenManager(new VelocityCharStream(source, 1, 1));
    }

    public Object getNextToken() {
        return vmParserTokenManager.getNextToken();
    }

    public void setFileName(final String fileName) {
        AbstractTokenManager.setFileName(fileName);
    }

}
