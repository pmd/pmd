/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

/**
 * Base implementation of an antlr rule.
 */
public abstract class AntlrBaseRule extends AbstractRule {

    protected AntlrBaseRule() {
        // inheritance constructor
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        AstVisitor<RuleContext, ?> visitor = buildVisitor();
        assert visitor != null : "Rule should provide a non-null visitor";
        assert target instanceof AntlrNode : "Incorrect node type " + target + " passed to " + this;

        ((AntlrNode<?>) target).acceptVisitor(visitor, ctx);
    }

    /**
     * Returns a rule visitor that can visit nodes for the given rule context.
     * This visitor should explore the nodes it's interested in and report
     * violations on the given rule context.
     *
     * @return A visitor bound to the given rule context
     */
    public abstract AstVisitor<RuleContext, ?> buildVisitor();

}
