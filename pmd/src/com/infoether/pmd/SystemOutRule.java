package com.infoether.pmd;

import com.infoether.pmd.ast.JavaParserVisitorAdapter;
import com.infoether.pmd.ast.ASTName;

import java.util.*;

public class SystemOutRule extends AbstractRule implements Rule {

    public String getDescription() {return "Don't use System.out/in/err, use the Cougaar logging service instead";}

    public Object visit(ASTName node, Object data){
        if (node.getImage() != null && (node.getImage().startsWith("System.out") || node.getImage().startsWith("System.err") || node.getImage().startsWith("System.in"))) {
            ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
        }
        return super.visit(node,data);
    }
}
