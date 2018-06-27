/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Adapter for the Apex jorje parser
 */
public class ApexParser extends AbstractParser {
    private net.sourceforge.pmd.lang.apex.ast.ApexParser apexParser;

    public ApexParser(ParserOptions parserOptions) {
        super(parserOptions);
        apexParser = new net.sourceforge.pmd.lang.apex.ast.ApexParser((ApexParserOptions) parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return null;
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        return apexParser.parse(source);
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return apexParser.getSuppressMap();
    }
}
