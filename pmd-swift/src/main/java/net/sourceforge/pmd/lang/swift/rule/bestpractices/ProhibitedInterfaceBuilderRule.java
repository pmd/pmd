/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.swift.AbstractSwiftRule;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser.FunctionHeadContext;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser.VariableDeclarationHeadContext;

public class ProhibitedInterfaceBuilderRule extends AbstractSwiftRule<Void> {

    private static final String IBACTION = "@IBAction";
    private static final String IBOUTLET = "@IBOutlet";

    public ProhibitedInterfaceBuilderRule() {
        super();
        addRuleChainVisit(FunctionHeadContext.class);
        addRuleChainVisit(VariableDeclarationHeadContext.class);
    }
    
    @Override
    public Void visitFunctionHead(FunctionHeadContext ctx) {
        if (ctx == null || ctx.attributes() == null) {
            return null;
        }

        return visitDeclarationHead(ctx, ctx.attributes().attribute(), IBACTION);
    }

    @Override
    public Void visitVariableDeclarationHead(final VariableDeclarationHeadContext ctx) {
        if (ctx == null || ctx.attributes() == null) {
            return null;
        }

        return visitDeclarationHead(ctx, ctx.attributes().attribute(), IBOUTLET);
    }

    private Void visitDeclarationHead(final Node node, final List<SwiftParser.AttributeContext> attributes,
        final String match) {

        final boolean violate = attributes.stream().anyMatch(atr -> match.equals(atr.getText()));
        if (violate) {
            addViolation(data, node);
        }

        return null;
    }
}
