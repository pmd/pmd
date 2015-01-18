/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.objectivec;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.objectivec.ast.ObjectiveCParserTokenManager;

/**
 * Objective-C Token Manager implementation.
 */
public class ObjectiveCTokenManager implements TokenManager {
    private final ObjectiveCParserTokenManager tokenManager;

    /**
     * Creates a new Objective-C Token Manager from the given source code.
     * @param source the source code
     */
    public ObjectiveCTokenManager(Reader source) {
        tokenManager = new ObjectiveCParserTokenManager(new SimpleCharStream(source));
    }

    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        ObjectiveCParserTokenManager.setFileName(fileName);
    }
}
