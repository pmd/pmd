/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

public class ProperCloneImplementationRule extends AbstractJavaRulechainRule {

    public ProperCloneImplementationRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (JavaAstUtils.isCloneMethod(method) && !method.isAbstract()) {
            ASTTypeDeclaration enclosingType = method.getEnclosingType();
            if (isNotFinal(enclosingType) && hasAnyAllocationOfClass(method, enclosingType)) {
                asCtx(data).addViolation(method);
            }
        }
        return data;
    }

    private boolean isNotFinal(ASTTypeDeclaration classOrInterfaceDecl) {
        return !classOrInterfaceDecl.hasModifiers(JModifier.FINAL);
    }

    private boolean hasAnyAllocationOfClass(ASTMethodDeclaration method, ASTTypeDeclaration enclosingType) {
        @NonNull
        JClassSymbol typeSymbol = enclosingType.getTypeMirror().getSymbol();
        return method.descendants(ASTConstructorCall.class)
            .filter(ctor -> ctor.getTypeMirror().getSymbol().equals(typeSymbol))
            .nonEmpty();
    }
}
