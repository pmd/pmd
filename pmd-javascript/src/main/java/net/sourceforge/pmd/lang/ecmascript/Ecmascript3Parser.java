/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTAstRoot;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;

/**
 * Adapter for the EcmascriptParser.
 */
public class Ecmascript3Parser implements Parser {
    private EcmascriptParser ecmascriptParser;

    public Ecmascript3Parser(EcmascriptParserOptions parserOptions) {
        super();
        ecmascriptParser = new EcmascriptParser(parserOptions);
    }

    @Override
    public ASTAstRoot parse(ParserTask task) throws ParseException {
        return ecmascriptParser.parse(task);
    }

}
