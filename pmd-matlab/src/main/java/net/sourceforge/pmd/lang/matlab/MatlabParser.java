/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Adapter for the Matlab Parser.
 *
 * @deprecated There is no full PMD support for Matlab.
 */
@Deprecated
public class MatlabParser extends AbstractParser {

    /**
     * Creates a new Matlab Parser.
     *
     * @param parserOptions
     *            the options
     */
    public MatlabParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new MatlabTokenManager(source);
    }

    @Override
    public boolean canParse() {
        return false;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        throw new UnsupportedOperationException("parse(Reader) is not supported for Matlab");
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        throw new UnsupportedOperationException("getSuppressMap() is not supported for Matlab");
    }
}
