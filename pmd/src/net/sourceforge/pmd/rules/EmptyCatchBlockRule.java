/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 12:13:55 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.*;

public class EmptyCatchBlockRule extends AbstractRule implements Rule {

    // TODO this only catches the first empty catch block of a try..catch stmt
    public Object visit(ASTTryStatement node, Object data){
        RuleContext ctx = (RuleContext)data;
        // this skips try..finally constructs since they don't have catch blocks
        if (!node.hasCatch()) {
            return super.visit(node, data);
        }
        if (node.getFirstCatchBlock().jjtGetNumChildren() == 0) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getFirstCatchBlock().getBeginLine()));
        }
        return super.visit(node, data);
    }
}
