/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * This is the base class for antlr generated parsers. The implementation
 * of PMD's {@link net.sourceforge.pmd.lang.Parser} interface is {@link AntlrBaseParser}.
 */
public abstract class AntlrGeneratedParserBase<N extends GenericNode<N>> extends Parser {

    public AntlrGeneratedParserBase(TokenStream input) {
        super(input);
    }


    @Override
    public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
        return createPmdTerminal(parent, t).asAntlrNode();
    }

    @Override
    public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
        return createPmdError(parent, t).asAntlrNode();
    }

    // Those two need to return a node that implements eg SwiftNode

    public abstract BaseAntlrTerminalNode<N> createPmdTerminal(ParserRuleContext parent, Token t);

    public abstract BaseAntlrErrorNode<N> createPmdError(ParserRuleContext parent, Token t);


    protected Node asPmdNode(RuleContext ctx) {
        return ((BaseAntlrNode.AntlrToPmdParseTreeAdapter<?>) ctx).getPmdNode();
    }

    // Necessary API to build the trees

    protected void enterRule(BaseAntlrInnerNode<N> ptree, int state, int alt) {
        enterRule(ptree.asAntlrNode(), state, alt);
    }

    protected void enterOuterAlt(BaseAntlrInnerNode<N> localctx, int altNum) {
        enterOuterAlt(localctx.asAntlrNode(), altNum);
    }

    protected void pushNewRecursionContext(BaseAntlrInnerNode<N> localctx, int state, int ruleIndex) {
        pushNewRecursionContext(localctx.asAntlrNode(), state, ruleIndex);
    }

    protected void enterRecursionRule(BaseAntlrInnerNode<N> localctx, int state, int ruleIndex, int precedence) {
        enterRecursionRule(localctx.asAntlrNode(), state, ruleIndex, precedence);

    }

    protected boolean sempred(BaseAntlrInnerNode<N> _localctx, int ruleIndex, int predIndex) {
        return sempred(_localctx.asAntlrNode(), ruleIndex, predIndex);
    }

}
