/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Reader;

/**
 * This is a generic implementation of the Parser interface.
 *
 * @see Parser
 *
 * @deprecated This will become useless in PMD 7. Implement or use {@link Parser} directly
 */
@Deprecated
public abstract class AbstractParser implements Parser {
    protected final ParserOptions parserOptions;

    public AbstractParser(ParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }

    @Override
    public ParserOptions getParserOptions() {
        return parserOptions;
    }

    @Override
    public TokenManager getTokenManager(String fileName, Reader source) {
        TokenManager tokenManager = createTokenManager(source);
        tokenManager.setFileName(fileName);
        return tokenManager;
    }

    protected abstract TokenManager createTokenManager(Reader source);
}
