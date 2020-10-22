/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import java.io.Reader;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTAstRoot;

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
    public ASTAstRoot parse(String fileName, Reader source) throws ParseException {
        return ecmascriptParser.parse(source);
    }

}
