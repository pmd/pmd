package net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatement;

public class UnnecessaryReturn extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {

        if (node.getResultType().isVoid()) {
            super.visit(node, data);
        }
        return data;
    }

    public Object visit(ASTReturnStatement node, Object data) {
        if (node.jjtGetParent().getClass().equals(ASTStatement.class) && node.jjtGetParent().jjtGetParent().getClass().equals(ASTBlockStatement.class) && node.jjtGetParent().jjtGetParent().jjtGetParent().getClass().equals(ASTBlock.class)
                && node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().getClass().equals(ASTMethodDeclaration.class)) {
            addViolation(data, node);
        }
        return data;
    }

}
