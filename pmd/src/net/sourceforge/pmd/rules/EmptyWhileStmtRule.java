/*
 * User: tom
 * Date: Jun 27, 2002
 * Time: 11:40:07 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.*;

public class EmptyWhileStmtRule extends AbstractRule implements Rule{
    public String getDescription() {return "Avoid empty 'while' statements";}

    /**
     * We're looking for anything other than
     * WhileStmt
     *  Expression
     *  Statement
     *   Block
     *    BlockStmt
     */
   public Object visit(ASTWhileStatement node, Object data){
       ASTStatement stmt = (ASTStatement)node.jjtGetChild(1);

       // can this happen?  an Statement without a child?
       if (stmt.jjtGetNumChildren() == 0) {
           return super.visit(node, data);
       }

       // an Statement whose child is not a Block... this might be caught be another braces type rule
       if (!(stmt.jjtGetChild(0) instanceof ASTBlock)) {
           return super.visit(node, data);
       }

        ASTBlock block = (ASTBlock)stmt.jjtGetChild(0);
        // this block must have 1 child, and that child must be a BlockStatement
       if (block.jjtGetNumChildren() == 0 || (!(block.jjtGetChild(0) instanceof ASTBlockStatement))) {
           (((RuleContext)data).getReport()).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
       }
       return super.visit(node, data);
    }
}
