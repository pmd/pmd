/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

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

}
