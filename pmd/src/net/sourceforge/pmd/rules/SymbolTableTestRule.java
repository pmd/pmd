package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        //System.out.println("final = " + node.isFinal());
        return super.visit(node,data);
    }

}
