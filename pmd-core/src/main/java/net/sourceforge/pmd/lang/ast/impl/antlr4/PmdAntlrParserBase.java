/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This is the base class for antlr generated parsers. The implementation
 * of PMD's {@link net.sourceforge.pmd.lang.Parser} interface is {@link AntlrBaseParser}.
 */
public abstract class PmdAntlrParserBase extends Parser {

    public PmdAntlrParserBase(TokenStream input) {
        super(input);
    }

    // these are made abstract because they should be overridden to return
    // terminals and error nodes that implement the language specific interface

    @Override
    public abstract TerminalNode createTerminalNode(ParserRuleContext parent, Token t);


    @Override
    public abstract ErrorNode createErrorNode(ParserRuleContext parent, Token t);
}
