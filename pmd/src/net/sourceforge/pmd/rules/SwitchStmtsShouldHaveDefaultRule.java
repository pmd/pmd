package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;

public class SwitchStmtsShouldHaveDefaultRule extends AbstractRule {

    public Object visit(ASTSwitchStatement node, Object data) {
        boolean foundDefaultLabel = false;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i) instanceof ASTSwitchLabel && node.jjtGetChild(i).jjtGetNumChildren() == 0) {
                foundDefaultLabel = true;
            }
        }
        if (!foundDefaultLabel) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }
}
