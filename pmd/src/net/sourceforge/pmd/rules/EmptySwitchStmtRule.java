/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 1:44:33 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTSwitchStatement;

public class EmptySwitchStmtRule extends AbstractRule {

    public Object visit(ASTSwitchStatement node, Object data) {
        if (node.jjtGetNumChildren() == 1) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }
}
