/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 4:23:30 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.*;

public class EmptyIfStmtRule extends AbstractRule implements Rule {

   public Object visit(ASTBlock node, Object data){
       RuleContext ctx = (RuleContext)data;
       if ((node.jjtGetParent().jjtGetParent() instanceof ASTIfStatement) && node.jjtGetNumChildren()==0) {
           ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
       }

        return super.visit(node, data);
    }
}
