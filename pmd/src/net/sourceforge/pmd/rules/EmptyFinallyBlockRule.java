/*
 * User: tom
 * Date: Jul 10, 2002
 * Time: 2:50:22 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;

public class EmptyFinallyBlockRule extends AbstractRule {
    public Object visit(ASTTryStatement node, Object data) {
        if (!node.hasFinally()) {
            return super.visit(node, data);
        }
        ASTBlock finallyBlock = node.getFinallyBlock();
        if (finallyBlock.jjtGetNumChildren() == 0) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, finallyBlock.getBeginLine()));
        }
        return super.visit(node, data);
    }

}
