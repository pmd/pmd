package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.SimpleNode;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode n = node.getTypeNameNode();
        System.out.println("n = " + n.getImage());
        return super.visit(node, data);
    }

}
