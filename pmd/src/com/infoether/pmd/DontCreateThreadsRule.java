package com.infoether.pmd;

import com.infoether.pmd.ast.JavaParserVisitorAdapter;
import com.infoether.pmd.ast.ASTAllocationExpression;
import com.infoether.pmd.ast.ASTName;

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
