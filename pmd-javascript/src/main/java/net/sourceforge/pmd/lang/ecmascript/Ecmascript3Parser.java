/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript5.Ecmascript5TokenManager;

/**
 * Adapter for the EcmascriptParser.
 */
public class Ecmascript3Parser extends AbstractParser {
    private net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser ecmascriptParser;

    public Ecmascript3Parser(ParserOptions parserOptions) {
        super(parserOptions);
        ecmascriptParser = new net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser(
                (EcmascriptParserOptions) parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new Ecmascript5TokenManager(source);
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        return ecmascriptParser.parse(source);
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return ecmascriptParser.getSuppressMap();
    }
}
