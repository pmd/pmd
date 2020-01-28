/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;


public class ModelicaTokenManager implements TokenManager {
    private final ModelicaParserImplTokenManager modelicaParserTokenManager;

    public ModelicaTokenManager(final Reader source) {
        modelicaParserTokenManager = new ModelicaParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
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
