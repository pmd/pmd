/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParserTokenManager;


/**
 * @deprecated This is internal API
 */
@Deprecated
@InternalApi
public class ModelicaTokenManager implements TokenManager {

    private final ModelicaParserTokenManager modelicaParserTokenManager;

    public ModelicaTokenManager(final Reader source) {
        modelicaParserTokenManager = new ModelicaParserTokenManager(new SimpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return modelicaParserTokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        AbstractTokenManager.setFileName(fileName);
    }
}
