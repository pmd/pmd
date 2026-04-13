/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.velocity.ast.VtlVisitor;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractVtlRule extends AbstractRule implements VtlVisitor<RuleContext, RuleContext> {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    @Override
    public RuleContext visitNode(Node node, RuleContext param) {
        node.children().forEach(it -> it.acceptVisitor(this, param));
        return param;
    }
}
