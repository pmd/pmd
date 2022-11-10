/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class InvalidJavaBeanRule extends AbstractJavaRule {

    // TODO: Add property "ensureSerialization"
    // TODO: Add property "package"

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!TypeTestUtil.isA(Serializable.class, node)) {
            asCtx(data).addViolationWithMessage(node, "The bean ''{0}'' does not implement java.io.Serializable.",
                    node.getSimpleName());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTClassOrInterfaceDeclaration enclosingClass = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (!node.isTransient() && !node.isStatic() && enclosingClass != null) {
            String beanName = enclosingClass.getSimpleName();
            for (ASTVariableDeclaratorId varId : node) {
                if (hasGetter(varId) && !hasSetter(varId)) {
                    asCtx(data).addViolationWithMessage(varId, "The bean ''{0}'' is missing a setter for property ''{1}''.",
                            beanName, varId.getName());
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean hasSetter(ASTVariableDeclaratorId varId) {
        // TODO: use scope / symbol table
        String propertyName = varId.getName();
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT) + propertyName.substring(1);
        List<ASTAnyTypeBodyDeclaration> declarations = varId.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getDeclarations();
        for (ASTAnyTypeBodyDeclaration declaration : declarations) {
            if (ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD == declaration.getKind()) {
                ASTMethodDeclaration method = declaration.getFirstChildOfType(ASTMethodDeclaration.class);
                if (method.getName().equals(setterName) && method.getArity() == 1) {
                    // TODO: check parameter type
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasGetter(ASTVariableDeclaratorId varId) {
        String propertyName = varId.getName();
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT) + propertyName.substring(1);
        List<ASTAnyTypeBodyDeclaration> declarations = varId.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getDeclarations();
        for (ASTAnyTypeBodyDeclaration declaration : declarations) {
            if (ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD == declaration.getKind()) {
                ASTMethodDeclaration method = declaration.getFirstChildOfType(ASTMethodDeclaration.class);
                if (method.getName().equals(getterName) && method.getArity() == 0) {
                    // TODO check result type
                    return true;
                }
            }
        }
        return false;
    }

}
