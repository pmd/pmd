package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTName;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTPrimarySuffix node, Object data) {
        System.out.println("ASTPrimarySuffix: image: " + node.getImage());
        return super.visit(node,data);
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {
        System.out.println("ASTPrimaryPrefix: image: " + node.getImage());
        return super.visit(node,data);
    }

    public Object visit(ASTName node, Object data) {
        System.out.println("ASTName: image: " + node.getImage());
        return super.visit(node,data);
    }
}
