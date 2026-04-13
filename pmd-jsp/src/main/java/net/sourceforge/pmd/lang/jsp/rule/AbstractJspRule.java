/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.JspVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractJspRule extends AbstractRule implements JspVisitor<RuleContext, RuleContext> {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    @Override
    public RuleContext visitNode(Node node, RuleContext param) {
        node.children().forEach(c -> c.acceptVisitor(this, param));
        return param;
    }
}
