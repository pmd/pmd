/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 3:34:04 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;

public class EmptyTryBlockRule extends AbstractRule{
    public Object visit(ASTTryStatement node, Object data){
        RuleContext ctx = (RuleContext)data;
        ASTBlock tryBlock = (ASTBlock)node.jjtGetChild(0);
        if (tryBlock.jjtGetNumChildren() == 0) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
