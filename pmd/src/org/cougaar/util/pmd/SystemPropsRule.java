package org.cougaar.util.pmd;

import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.*;

import java.util.*;

public class SystemPropsRule extends AbstractRule implements Rule {

    private boolean inGetPropsCtx;

    public Object visit(ASTName node, Object data){
        if (node.getImage() != null && node.getImage().startsWith("System.setProperty") || node.getImage().startsWith("System.getProperties")) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
            return super.visit(node,data);
        }
        if (node.getImage() != null && (node.getImage().startsWith("System.getProperty"))) {
            inGetPropsCtx = true;
        }
        return super.visit(node,data);
    }

    public Object visit(ASTLiteral node, Object data) {
        if (inGetPropsCtx && (node.getImage() != null) && (!node.getImage().startsWith("\"org.cougaar"))) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        inGetPropsCtx = false;
        return super.visit(node,data);
    }
}
