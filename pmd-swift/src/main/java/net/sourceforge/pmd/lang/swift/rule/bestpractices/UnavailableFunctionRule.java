/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftInnerNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.AttributeContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.AttributesContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.CodeBlockContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.FunctionDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.InitializerDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.StatementContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;

public class UnavailableFunctionRule extends AbstractSwiftRule {

    private static final String AVAILABLE_UNAVAILABLE = "@available(*,unavailable)";
    private static final String FATAL_ERROR = "fatalError";

    public UnavailableFunctionRule() {
        super();
        addRuleChainVisit(SwiftTreeParser.RULE_functionDeclaration);
        addRuleChainVisit(SwiftTreeParser.RULE_initializerDeclaration);
    }

    @Override
    public SwiftVisitor<RuleContext, Void> buildVisitor() {
        return new SwiftVisitor<RuleContext, Void>() {

            @Override
            public Void visitAnyNode(SwiftNode<?> swiftNode, RuleContext data) {
                return null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public Void visitInnerNode(SwiftInnerNode<?> swiftNode, RuleContext data) {
                switch (swiftNode.getParseTree().getRuleIndex()) {
                case SwiftTreeParser.RULE_functionDeclaration:
                    return visitFunctionDeclaration((SwiftNode<FunctionDeclarationContext>) swiftNode, data);
                case SwiftTreeParser.RULE_initializerDeclaration:
                    return visitInitializerDeclaration((SwiftNode<InitializerDeclarationContext>) swiftNode, data);
                }
                return null;
            }

            public Void visitFunctionDeclaration(final SwiftNode<FunctionDeclarationContext> ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.getParseTree().functionBody().codeBlock())) {
                    final AttributesContext attributes = ctx.getParseTree().functionHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            public Void visitInitializerDeclaration(final SwiftNode<InitializerDeclarationContext> ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.getParseTree().initializerBody().codeBlock())) {
                    final AttributesContext attributes = ctx.getParseTree().initializerHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            private boolean shouldIncludeUnavailableModifier(final CodeBlockContext ctx) {
                if (ctx == null || ctx.statements() == null) {
                    return false;
                }

                final List<StatementContext> statements = ctx.statements().statement();

                return statements.size() == 1 && FATAL_ERROR.equals(statements.get(0).getStart().getText());
            }

            private boolean hasUnavailableModifier(final List<AttributeContext> attributes) {
                return attributes.stream().anyMatch(atr -> AVAILABLE_UNAVAILABLE.equals(atr.getText()));
            }
        };
    }

}
