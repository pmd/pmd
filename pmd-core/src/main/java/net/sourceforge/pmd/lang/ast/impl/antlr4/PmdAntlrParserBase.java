/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

/**
 * This is the base class for antlr generated parsers. The implementation
 * of PMD's {@link net.sourceforge.pmd.lang.Parser} interface is {@link AntlrBaseParser}.
 */
public abstract class PmdAntlrParserBase extends Parser {

    public PmdAntlrParserBase(TokenStream input) {
        super(input);
    }


    protected void enterRule(AntlrParseTreeBase ptree, int state, int alt) {

    }

    public void enterOuterAlt(AntlrParseTreeBase localctx, int altNum) {

    }


    public void pushNewRecursionContext(AntlrParseTreeBase localctx, int state, int ruleIndex) {

    }

    public void enterRecursionRule(AntlrParseTreeBase localctx, int state, int ruleIndex, int precedence) {

    }

}
