/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class OverrideBothEqualsAndHashcodeRule extends AbstractJavaRulechainRule {

    public OverrideBothEqualsAndHashcodeRule() {
        super(ASTClassOrInterfaceDeclaration.class,
              ASTRecordDeclaration.class,
              ASTAnonymousClassDeclaration.class);
    }

    private void visitTypeDecl(ASTAnyTypeDeclaration node, Object data) {
        if (TypeTestUtil.isA(Comparable.class, node)) {
            return;
        }
        ASTMethodDeclaration equalsMethod = null;
        ASTMethodDeclaration hashCodeMethod = null;
        for (ASTMethodDeclaration m : node.getDeclarations(ASTMethodDeclaration.class)) {
            if (JavaAstUtils.isEqualsMethod(m)) {
                equalsMethod = m;
                if (hashCodeMethod != null) {
                    break; // shortcut
                }
            } else if (JavaAstUtils.isHashCodeMethod(m)) {
                hashCodeMethod = m;
                if (equalsMethod != null) {
                    break; // shortcut
                }
            }
        }

        if (hashCodeMethod != null ^ equalsMethod != null) {
            ASTMethodDeclaration nonNullNode =
                equalsMethod == null ? hashCodeMethod : equalsMethod;
            asCtx(data).addViolation(nonNullNode);
        }
    }

    @Override
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return null;
        }
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }
}
