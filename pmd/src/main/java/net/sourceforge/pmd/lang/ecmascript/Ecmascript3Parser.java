/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Adapter for the EcmascriptParser.
 */
public class Ecmascript3Parser extends AbstractParser {

    public Ecmascript3Parser(ParserOptions parserOptions) {
	super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
	return null;
    }

    public boolean canParse() {
	return true;
    }

    public Node parse(String fileName, Reader source) throws ParseException {
	return new net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser((EcmascriptParserOptions)parserOptions).parse(source);
    }

    public Map<Integer, String> getSuppressMap() {
	return new HashMap<Integer, String>(); // FIXME
    }
}
