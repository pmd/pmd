package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTForInit;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTForUpdate;


/**
 * @author sandymac
 */
public class ForLoopShouldBeWhileLoopRule extends AbstractRule {
    public Object visit(ASTForStatement node, Object data) {
        if (node.jjtGetNumChildren() > 1 && !forLoopHasInitOrUpdate(node)) {
            RuleContext ctx = (RuleContext) data;
            RuleViolation rv = createRuleViolation(ctx, node.getBeginLine());
            ctx.getReport().addRuleViolation(rv);
        }

        return super.visit(node, data);
    }

    private boolean forLoopHasInitOrUpdate(ASTForStatement node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i) instanceof ASTForInit || node.jjtGetChild(i) instanceof ASTForUpdate) {
                return true;
            }
        }
        return false;
    }
}