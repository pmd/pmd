package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.*;

import java.util.*;

public class SystemOutRule extends AbstractRule implements Rule {

    public String getMessage() {return "Don't use System.out/in/err, use the Cougaar logging service instead";}

    public Object visit(ASTName node, Object data){
        RuleContext ctx = (RuleContext)data;
        if (node.getImage() != null && (node.getImage().startsWith("System.out") || node.getImage().startsWith("System.err") || node.getImage().startsWith("System.in"))) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node,data);
    }
}
