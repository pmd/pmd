/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

public class ApexParser extends AbstractParser {

    public ApexParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new ApexTokenManager(source);
    }

    @Override
    public boolean canParse() {
        return false;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        throw new UnsupportedOperationException("parse(Reader) is not supported for Apex");
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        throw new UnsupportedOperationException("getSuppressMap() is not supported for Apex");
    }
}
