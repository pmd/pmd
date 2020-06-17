/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwAttribute;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwAttributes;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwCodeBlock;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwFunctionDeclaration;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwInitializerDeclaration;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwStatement;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitorBase;

public class UnavailableFunctionRule extends AbstractSwiftRule {

    private static final String AVAILABLE_UNAVAILABLE = "@available(*,unavailable)";
    private static final String FATAL_ERROR = "fatalError";

    public UnavailableFunctionRule() {
        super();
        addRuleChainVisit(SwFunctionDeclaration.class);
        addRuleChainVisit(SwInitializerDeclaration.class);
    }

    @Override
    public AstVisitor<RuleContext, ?> buildVisitor() {
        return new SwiftVisitorBase<RuleContext, Void>() {

            @Override
            public Void visitFunctionDeclaration(final SwFunctionDeclaration ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.functionBody().codeBlock())) {
                    final SwAttributes attributes = ctx.functionHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            @Override
            public Void visitInitializerDeclaration(final SwInitializerDeclaration ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.initializerBody().codeBlock())) {
                    final SwAttributes attributes = ctx.initializerHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        addViolation(ruleCtx, ctx);
                    }
                }

                return null;
            }

            private boolean shouldIncludeUnavailableModifier(final SwCodeBlock ctx) {
                if (ctx == null || ctx.statements() == null) {
                    return false;
                }

                final List<SwStatement> statements = ctx.statements().statement();

                return statements.size() == 1 && FATAL_ERROR.equals(statements.get(0).getFirstAntlrToken().getText());
            }

            private boolean hasUnavailableModifier(final List<SwAttribute> attributes) {
                return attributes.stream().anyMatch(atr -> AVAILABLE_UNAVAILABLE.equals(atr.joinTokenText()));
            }
        };
    }

}
