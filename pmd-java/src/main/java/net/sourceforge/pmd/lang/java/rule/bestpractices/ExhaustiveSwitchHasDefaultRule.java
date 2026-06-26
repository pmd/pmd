/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.10.0 (as XPath) / 7.27.0 (as Java)
 */
public class ExhaustiveSwitchHasDefaultRule extends AbstractJavaRulechainRule {
    public ExhaustiveSwitchHasDefaultRule() {
        super(ASTSwitchExpression.class, ASTStatement.class);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    private void visitSwitchLike(ASTSwitchLike node, RuleContext ctx) {
        if (node.isExhaustive() && node.hasDefaultCase()) {
            ctx.addViolation(node);
        }
    }
}
