/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser;

public class UnavailableFunctionRule extends AbstractSwiftRule<Void> {

    private static final String AVAILABLE_UNAVAILABLE = "@available(*,unavailable)";
    private static final String FATAL_ERROR = "fatalError";

    @Override
    public Void visitFunctionDeclaration(final SwiftParser.FunctionDeclarationContext ctx) {
        if (ctx == null) {
            return null;
        }

        if (shouldIncludeUnavailableModifier(ctx.functionBody().codeBlock())) {
            final SwiftParser.AttributesContext attributes = ctx.functionHead().attributes();
            if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                addViolation(data, ctx);
            }
        }

        return null;
    }

    @Override
    public Void visitInitializerDeclaration(final SwiftParser.InitializerDeclarationContext ctx) {
        if (ctx == null) {
            return null;
        }

        if (shouldIncludeUnavailableModifier(ctx.initializerBody().codeBlock())) {
            final SwiftParser.AttributesContext attributes = ctx.initializerHead().attributes();
            if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                addViolation(data, ctx);
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
}
