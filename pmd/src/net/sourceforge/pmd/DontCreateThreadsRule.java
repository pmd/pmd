package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTName;

import java.util.*;

public class DontCreateThreadsRule extends AbstractRule implements Rule {

    public String getDescription() {return "Don't create threads, use the ThreadService instead";}

    public Object visit(ASTAllocationExpression node, Object data){
        if ((node.jjtGetChild(0) instanceof ASTName)  // this avoids "new <primitive-type>", i.e., "new int[]"
                && ((ASTName)node.jjtGetChild(0)).getImage().equals("Thread")) {
            ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
