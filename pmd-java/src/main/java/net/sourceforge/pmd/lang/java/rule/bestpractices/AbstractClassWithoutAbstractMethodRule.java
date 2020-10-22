/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class AbstractClassWithoutAbstractMethodRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!node.isAbstract() || doesExtend(node) || doesImplement(node)) {
            return data;
        }

        int countOfAbstractMethods = 0;
        for (ASTMethodDeclaration methodDecl : node.descendants(ASTMethodDeclaration.class)) {
            if (methodDecl.isAbstract()) {
                countOfAbstractMethods++;
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
