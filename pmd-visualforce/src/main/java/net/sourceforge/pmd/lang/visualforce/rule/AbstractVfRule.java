/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.visualforce.ast.VfVisitor;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractVfRule extends AbstractRule implements VfVisitor<Object, Object> {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    @Override
    public Object visitNode(Node node, Object param) {
        node.children().forEach(n -> n.acceptVisitor(this, param));
        return param;
    }
}
