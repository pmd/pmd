/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ProperCloneImplementationRule extends AbstractJavaRule {

    public ProperCloneImplementationRule() {
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (isCloneMethod(method) && isNotAbstractMethod(method)) {
            ASTClassOrInterfaceDeclaration classDecl = method.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (isNotFinal(classDecl) && hasAnyAllocationOfClass(method, classDecl.getSimpleName())) {
                addViolation(data, method);
            }
        }
        return data;
    }

    private boolean isCloneMethod(ASTMethodDeclaration method) {
        return "clone".equals(method.getName()) && method.getArity() == 0;
    }

    private boolean isNotAbstractMethod(ASTMethodDeclaration method) {
        return !method.isAbstract();
    }

    private boolean isNotFinal(ASTClassOrInterfaceDeclaration classOrInterfaceDecl) {
        return !classOrInterfaceDecl.isFinal();
    }

    private boolean hasAnyAllocationOfClass(ASTMethodDeclaration method, String className) {
        List<ASTAllocationExpression> allocations = method.findDescendantsOfType(ASTAllocationExpression.class);
        for (ASTAllocationExpression allocation : allocations) {
            ASTClassOrInterfaceType allocatedType = allocation.getFirstChildOfType(ASTClassOrInterfaceType.class);
            if (isSimpleNameOfType(className, allocatedType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSimpleNameOfType(String simpleName, ASTClassOrInterfaceType type) {
        return type != null && type.hasImageEqualTo(simpleName);
    }
}
