/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 12:13:55 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.*;

public class EmptyCatchBlockRule extends AbstractRule implements Rule {

    public String getDescription() {return "Avoid empty catch blocks";}

    public Object visit(ASTTryStatement node, Object data){
        RuleContext ctx = (RuleContext)data;
        // this skips try..finally constructs since they don't have catch blocks
        if (node.jjtGetNumChildren() < 3) {
            return super.visit(node, data);
        }
        ASTBlock catchBlock = (ASTBlock)node.jjtGetChild(2);
        if (catchBlock.jjtGetNumChildren() == 0) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
