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

public abstract class PmdAntlrParserBase extends Parser {

    public PmdAntlrParserBase(TokenStream input) {
        super(input);
    }

    @Override
    public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
        return new PmdAntlrTerminalNode(t);
    }

    @Override
    public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
        return new PmdAntlrErrorNode(t);
    }
}
