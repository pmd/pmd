package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTName;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTInstanceOfExpression node, Object data) {
        ASTType type = (ASTType)node.jjtGetChild(1);
        ASTName name = (ASTName)type.jjtGetChild(0);
        System.out.println("name = " + name.getImage());
        return super.visit(node, data);
    }

}
