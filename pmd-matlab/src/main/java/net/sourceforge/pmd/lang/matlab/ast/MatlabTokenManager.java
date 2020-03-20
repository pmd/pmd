/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Matlab Token Manager implementation.
 */
@InternalApi
public class MatlabTokenManager implements TokenManager {
    private final MatlabParserImplTokenManager tokenManager;

    public MatlabTokenManager(Reader source) {
        tokenManager = new MatlabParserImplTokenManager(CharStreamFactory.simpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }


    public static TokenManager<JavaccToken> create(Reader reader) {
        return new MatlabParserImplTokenManager(CharStreamFactory.simpleCharStream(reader));
    }

}
