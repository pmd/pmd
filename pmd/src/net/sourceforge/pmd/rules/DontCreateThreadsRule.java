package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.*;

import java.util.*;

public class DontCreateThreadsRule extends AbstractRule implements Rule {

    public String getDescription() {return "Don't create threads, use the ThreadService instead";}

    public Object visit(ASTAllocationExpression node, Object data){
        RuleContext ctx = (RuleContext)data;
        if ((node.jjtGetChild(0) instanceof ASTName)  // this avoids "new <primitive-type>", i.e., "new int[]"
                && ((ASTName)node.jjtGetChild(0)).getImage().equals("Thread")) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
