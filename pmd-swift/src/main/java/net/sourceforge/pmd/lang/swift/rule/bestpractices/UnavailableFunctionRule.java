/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.FunctionDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.InitializerDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;

public class UnavailableFunctionRule extends AbstractSwiftRule {

    private static final String AVAILABLE_UNAVAILABLE = "@available(*,unavailable)";
    private static final String FATAL_ERROR = "fatalError";

    public UnavailableFunctionRule() {
        super();
        addRuleChainVisit(SwiftParser.RULE_functionDeclaration);
        addRuleChainVisit(SwiftParser.RULE_initializerDeclaration);
    }

    @Override
    public SwiftVisitor<RuleContext, Void> buildVisitor() {
        return new SwiftVisitor<RuleContext, Void>() {

            @Override
            @SuppressWarnings("unchecked")
            public Void visitAnyNode(SwiftNode<?> swiftNode, RuleContext data) {
                switch (swiftNode.getParseTree().getRuleIndex()) {
                case SwiftParser.RULE_functionDeclaration:
                    return visitFunctionDeclaration((SwiftNode<FunctionDeclarationContext>) swiftNode, data);
                case SwiftParser.RULE_initializerDeclaration:
                    return visitInitializerDeclaration((SwiftNode<InitializerDeclarationContext>) swiftNode, data);
                }
                return null;
            }

            public Void visitFunctionDeclaration(final SwiftNode<FunctionDeclarationContext> ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.getParseTree().functionBody().codeBlock())) {
                    final SwiftParser.AttributesContext attributes = ctx.getParseTree().functionHead().attributes();
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
                    final SwiftParser.AttributesContext attributes = ctx.getParseTree().initializerHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            private boolean shouldIncludeUnavailableModifier(final SwiftParser.CodeBlockContext ctx) {
                if (ctx == null || ctx.statements() == null) {
                    return false;
                }

                final List<SwiftParser.StatementContext> statements = ctx.statements().statement();

                return statements.size() == 1 && FATAL_ERROR.equals(statements.get(0).getStart().getText());
            }

            private boolean hasUnavailableModifier(final List<SwiftParser.AttributeContext> attributes) {
                return attributes.stream().anyMatch(atr -> AVAILABLE_UNAVAILABLE.equals(atr.getText()));
            }
        };
    }

}
