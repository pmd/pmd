/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 4:23:30 PM
 */
package com.infoether.pmd;

import com.infoether.pmd.ast.JavaParserVisitorAdapter;
import com.infoether.pmd.ast.ASTBlock;
import com.infoether.pmd.ast.ASTTryStatement;
import com.infoether.pmd.ast.ASTIfStatement;

public class EmptyIfStmtRule extends AbstractRule implements Rule {
    public String getDescription() {return "Avoid empty IF statements";}

   public Object visit(ASTBlock node, Object data){
       if ((node.jjtGetParent().jjtGetParent() instanceof ASTIfStatement) && node.jjtGetNumChildren()==0) {
           ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
       }

        return super.visit(node, data);
    }
}
