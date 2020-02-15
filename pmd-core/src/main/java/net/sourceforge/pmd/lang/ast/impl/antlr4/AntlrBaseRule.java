/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.RuleContext;
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
        AbstractAntlrVisitor<Void> visitor = buildVisitor(ctx);
        assert visitor != null : "Rule should provide a non-null visitor";
        assert target instanceof AntlrBaseNode : "Incorrect node type " + target + " passed to " + this;

        ((AntlrBaseNode) target).accept(visitor);
    }

    /**
     * Returns a rule visitor that can visit nodes for the given rule context.
     * This visitor should explore the nodes it's interested in and report
     * violations on the given rule context.
     *
     * @param ruleCtx Object that accumulates rule violations
     *
     * @return A visitor bound to the given rule context
     */
    public abstract AbstractAntlrVisitor<Void> buildVisitor(RuleContext ruleCtx);

}
