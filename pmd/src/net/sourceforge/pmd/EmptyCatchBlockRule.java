/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 12:13:55 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;

public class EmptyCatchBlockRule extends AbstractRule implements Rule {

    public String getDescription() {return "Avoid empty catch blocks";}

   public Object visit(ASTBlock node, Object data){
       if ((node.jjtGetParent() instanceof ASTTryStatement) && node.jjtGetNumChildren()==0) {
           ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
       }

        return super.visit(node, data);
    }
}
