/*
 * User: tom
 * Date: Jul 10, 2002
 * Time: 2:50:22 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.SimpleNode;

public class EmptyFinallyBlockRule extends AbstractRule {
    public Object visit(ASTTryStatement node, Object data){
        if (!node.hasFinally()) {
            return super.visit(node, data);
        }

        // assume this is a try..finally construct
        int finallyNodeIndex = 1;
        if (node.hasCatch()) {
            // jump to the third child since there's a FormalParameter between the catch Block and the finally Block
            finallyNodeIndex = 3;
        }
        SimpleNode finallyBlock = (SimpleNode)node.jjtGetChild(finallyNodeIndex);
        if (finallyBlock.jjtGetNumChildren() == 0) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, finallyBlock.getBeginLine()));
        }
        return super.visit(node, data);
    }

}
