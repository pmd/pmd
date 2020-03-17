/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python.ast;

import java.io.Reader;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

/**
 * Python Token Manager implementation.
 */
public class PythonTokenManager implements TokenManager {
    private final PythonParserImplTokenManager tokenManager;

    /**
     * Creates a new Python Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public PythonTokenManager(Reader source) {
        tokenManager = new PythonParserImplTokenManager(CharStreamFactory.simpleCharStream(source, PythonTokenDocument::new));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        PythonParserImplTokenManager.setFileName(fileName);
    }

    private static class PythonTokenDocument extends JavaccTokenDocument {

        PythonTokenDocument(String fullText) {
            super(fullText);
        }

        @Override
        protected @Nullable String describeKindImpl(int kind) {
            return PythonTokenKinds.describe(kind);
        }

    }
}
