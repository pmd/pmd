/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

public class ProperCloneImplementationRule extends AbstractJavaRulechainRule {

    public ProperCloneImplementationRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (isCloneMethod(method) && isNotAbstractMethod(method)) {
            ASTAnyTypeDeclaration enclosingType = method.getEnclosingType();
            if (isNotFinal(enclosingType) && hasAnyAllocationOfClass(method, enclosingType)) {
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

    private boolean isNotFinal(ASTAnyTypeDeclaration classOrInterfaceDecl) {
        return !classOrInterfaceDecl.hasModifiers(JModifier.FINAL);
    }

    private boolean hasAnyAllocationOfClass(ASTMethodDeclaration method, ASTAnyTypeDeclaration enclosingType) {
        @NonNull
        JClassSymbol typeSymbol = enclosingType.getTypeMirror().getSymbol();
        return method.descendants(ASTConstructorCall.class)
            .filter(ctor -> ctor.getTypeMirror().getSymbol().equals(typeSymbol))
            .nonEmpty();
    }
}
