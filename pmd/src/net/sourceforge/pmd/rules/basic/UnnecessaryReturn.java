package net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryReturn extends AbstractJavaRule {

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
