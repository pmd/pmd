/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;


/**
 * @deprecated This is internal API
 */
@Deprecated
@InternalApi
public class ModelicaTokenManager implements TokenManager {

    private final ModelicaParserImplTokenManager modelicaParserTokenManager;

    public ModelicaTokenManager(final Reader source) {
        modelicaParserTokenManager = new ModelicaParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return modelicaParserTokenManager.getNextToken();
    }

}
