/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.python.ast.PythonParserTokenManager;

/**
 * Python Token Manager implementation.
 *
 * @deprecated This is internal API, use {@link Tokenizer#tokenize(net.sourceforge.pmd.cpd.SourceCode, net.sourceforge.pmd.cpd.Tokens)}
 *             via {@link Language#getTokenizer()}.
 */
@Deprecated
@InternalApi
public class PythonTokenManager implements TokenManager {
    private final PythonParserTokenManager tokenManager;

    /**
     * Creates a new Python Token Manager from the given source code.
     *
     * @param source
     *            the source code
     */
    public PythonTokenManager(Reader source) {
        tokenManager = new PythonParserTokenManager(new SimpleCharStream(source));
    }

    @Override
    public Object getNextToken() {
        return tokenManager.getNextToken();
    }

    @Override
    public void setFileName(String fileName) {
        PythonParserTokenManager.setFileName(fileName);
    }
}
