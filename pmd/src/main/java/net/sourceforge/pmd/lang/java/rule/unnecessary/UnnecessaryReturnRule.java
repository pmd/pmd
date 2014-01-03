/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unnecessary;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryReturnRule extends AbstractJavaRule {

    public Object visit(ASTMethodDeclaration node, Object data) {

        if (node.getResultType().isVoid()) {
            super.visit(node, data);
        }
        return data;
    }

    public Object visit(ASTReturnStatement node, Object data) {
        if (node.jjtGetParent() instanceof ASTStatement && node.getNthParent(2) instanceof ASTBlockStatement && node.getNthParent(3) instanceof ASTBlock
                && node.getNthParent(4) instanceof ASTMethodDeclaration) {
            addViolation(data, node);
        }
        return data;
    }

}
