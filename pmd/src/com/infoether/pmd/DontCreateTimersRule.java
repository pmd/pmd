package com.infoether.pmd;

import com.infoether.pmd.ast.JavaParserVisitorAdapter;
import com.infoether.pmd.ast.ASTAllocationExpression;
import com.infoether.pmd.ast.ASTName;

import java.util.*;

public class DontCreateTimersRule extends AbstractRule implements Rule {

    public String getDescription() {return "Don't create java.util.Timers, use the Cougaar alarm service instead";}

    public Object visit(ASTAllocationExpression node, Object data){
        if ((node.jjtGetChild(0) instanceof ASTName)  // this avoids "new <primitive-type>", i.e., "new int[]"
                && ((ASTName)node.jjtGetChild(0)).getImage().equals("Timer")) {
            ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
