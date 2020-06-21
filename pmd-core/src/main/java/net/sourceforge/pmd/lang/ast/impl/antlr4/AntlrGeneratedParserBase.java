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

/**
 * This is the base class for antlr generated parsers. The implementation
 * of PMD's {@link net.sourceforge.pmd.lang.Parser} interface is {@link AntlrBaseParser}.
 *
 * <p>This class must implement the two abstract methods to create terminals
 * and error nodes that implement {@code <N>}. The inner nodes implement PMD
 * interfaces, and manipulation methods that the {@link Parser} superclass
 * uses are redirected to the underlying antlr {@link ParserRuleContext} (the
 * protected overloads here).
 *
 * <p>This is not enough in general to make the generated parser compilable,
 * so an ant script does some cleanup at the end.
 *
 * <p>Additionally this must have a {@link AntlrNameDictionary} static final field,
 * which stores the XPath names of the generated nodes (and terminals).
 *
 * <p>Additional members can be added to a parser with {@code @parser::members { ... }}
 * in the g4 file.
 */
public abstract class AntlrGeneratedParserBase<N extends AntlrNode<N>> extends Parser {

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

    protected boolean sempred(BaseAntlrInnerNode<N> localctx, int ruleIndex, int predIndex) {
        return sempred(localctx.asAntlrNode(), ruleIndex, predIndex);
    }

}
