/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser;

public class ProhibitedInterfaceBuilderRule extends AbstractSwiftRule<Boolean> {
    @Override
    public Boolean visitFunctionDeclaration(SwiftParser.FunctionDeclarationContext ctx) {
        Boolean isOverride = this.visitFunctionHead(ctx.functionHead());
        if (isOverride) {
            addViolation(data, ctx);
        }
        System.out.println();
        return true;
    }

    @Override
    public Boolean visitFunctionHead(SwiftParser.FunctionHeadContext ctx) {
        if (ctx == null) {
            return false;
        }
        SwiftParser.DeclarationModifiersContext declarationModifiers = ctx.declarationModifiers();
        if (declarationModifiers == null) {
            return false;
        }
        List<SwiftParser.DeclarationModifierContext> modifiers = ctx.declarationModifiers().declarationModifier();
        return modifiers.stream().anyMatch(modifier -> modifier.getText().equals("override"));
    }
}
