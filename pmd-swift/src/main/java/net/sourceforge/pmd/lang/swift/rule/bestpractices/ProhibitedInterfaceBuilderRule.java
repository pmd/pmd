/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser;

public class ProhibitedInterfaceBuilderRule extends AbstractSwiftRule<Boolean> {

    private static final String IBACTION = "@IBAction";
    private static final String IBOUTLET = "@IBOutlet";

    @Override
    public Boolean visitFunctionHead(SwiftParser.FunctionHeadContext ctx) {
        if (ctx == null || ctx.attributes() == null) {
            return false;
        }

        return visitDeclarationHead(ctx, ctx.attributes().attribute(), IBACTION);
    }

    @Override
    public Boolean visitVariableDeclarationHead(final SwiftParser.VariableDeclarationHeadContext ctx) {
        if (ctx == null || ctx.attributes() == null) {
            return false;
        }

        return visitDeclarationHead(ctx, ctx.attributes().attribute(), IBOUTLET);
    }

    private boolean visitDeclarationHead(final Node node, final List<SwiftParser.AttributeContext> attributes,
        final String match) {

        final boolean violate = attributes.stream().anyMatch(atr -> match.equals(atr.getText()));
        if (violate) {
            addViolation(data, node);
        }

        return violate;
    }
}
