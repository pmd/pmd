/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeBuilderState;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

/**
 *
 */
public class SwiftTreeBuilder extends SwiftBaseListener {

    final AntlrTreeBuilderState<SwiftNodeImpl<?>> state = new AntlrTreeBuilderState<SwiftNodeImpl<?>>() {
        @Override
        protected SwiftNodeImpl<?> defaultNode(AntlrParseTreeBase parseTree) {
            return new SwiftNodeImpl<>(parseTree);
        }
    };

    @Override
    public void enterTopLevel(TopLevelContext ctx) {
        state.setNodeToPush(new SwiftRootNode(ctx));
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        state.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        state.exitEveryRule(ctx);
    }
}
