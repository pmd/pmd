package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.*;

public class SystemPropsRule extends AbstractRule implements Rule {

    public String getDescription() {return "Don't use System.getProperty()/getProperties()/setProperty";}

    public Object visit(ASTName node, Object data){
        if (node.getImage() != null && (node.getImage().startsWith("System.getProperty") || node.getImage().startsWith("System.setProperty") || node.getImage().startsWith("System.getProperties"))) {
            ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
        }
        return super.visit(node,data);
    }
}
