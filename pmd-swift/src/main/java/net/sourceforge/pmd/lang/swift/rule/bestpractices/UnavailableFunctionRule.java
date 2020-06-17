/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.FunctionDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.InitializerDeclarationContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitorBase;

public class UnavailableFunctionRule extends AbstractSwiftRule {

    private static final String AVAILABLE_UNAVAILABLE = "@available(*,unavailable)";
    private static final String FATAL_ERROR = "fatalError";

    public UnavailableFunctionRule() {
        super();
        addRuleChainVisit(FunctionDeclarationContext.class);
        addRuleChainVisit(InitializerDeclarationContext.class);
    }

    @Override
    public AstVisitor<RuleContext, ?> buildVisitor() {
        return new SwiftVisitorBase<RuleContext, Void>() {

            @Override
            public Void visitFunctionDeclaration(final FunctionDeclarationContext ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.functionBody().codeBlock())) {
                    final SwiftParser.AttributesContext attributes = ctx.functionHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            @Override
            public Void visitInitializerDeclaration(final InitializerDeclarationContext ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.initializerBody().codeBlock())) {
                    final SwiftParser.AttributesContext attributes = ctx.initializerHead().attributes();
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

                return statements.size() == 1 && FATAL_ERROR.equals(statements.get(0).getFirstAntlrToken().getText());
            }

            private boolean hasUnavailableModifier(final List<SwiftParser.AttributeContext> attributes) {
                return attributes.stream().anyMatch(atr -> AVAILABLE_UNAVAILABLE.equals(atr.joinTokenText()));
            }
        };
    }

}
