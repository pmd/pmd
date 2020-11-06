/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AbstractClassWithoutAbstractMethodRule extends AbstractJavaRule {

    public AbstractClassWithoutAbstractMethodRule() {
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!node.isAbstract() || doesExtend(node) || doesImplement(node)) {
            return data;
        }

        int countOfAbstractMethods = 0;
        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.getKind() == DeclarationKind.METHOD) {
                ASTMethodDeclaration methodDecl = (ASTMethodDeclaration) decl.getDeclarationNode();
                if (methodDecl.isAbstract()) {
                    countOfAbstractMethods++;
                }
            }
        }
        if (countOfAbstractMethods == 0) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean doesExtend(ASTClassOrInterfaceDeclaration node) {
        return node.getFirstChildOfType(ASTExtendsList.class) != null;
    }

    private boolean doesImplement(ASTClassOrInterfaceDeclaration node) {
        return node.getFirstChildOfType(ASTImplementsList.class) != null;
    }
}
