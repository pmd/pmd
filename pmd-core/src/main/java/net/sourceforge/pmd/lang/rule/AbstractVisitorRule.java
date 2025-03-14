/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractVisitorRule extends AbstractRule {
    @Override
    public void apply(Node target, RuleContext ctx) {
        AstVisitor<RuleContext, ?> visitor = buildVisitor();
        assert visitor != null : "Rule should provide a non-null visitor";

        target.acceptVisitor(visitor, ctx);
    }

    /**
     * Returns a rule visitor that can visit nodes for the given rule context.
     * This visitor should explore the nodes it's interested in and report
     * violations on the given rule context.
     * <p>
     *     Language specific subclasses should redefine the return type to use
     *     a language specific visitor interface.
     * </p>
     *
     * @return A visitor bound to the given rule context
     */
    public abstract AstVisitor<RuleContext, ?> buildVisitor();
}
