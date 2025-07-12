/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * @since 7.16.0
 */
public class MissingEqualsOnComparableRule extends AbstractJavaRulechainRule {

    public MissingEqualsOnComparableRule() {
        super(ASTClassDeclaration.class,
                ASTRecordDeclaration.class,
                ASTAnonymousClassDeclaration.class);
    }

    @Override
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    private void visitTypeDecl(ASTTypeDeclaration node, Object data) {
        if (node.isInterface() || node.isAbstract()) {
            return;
        }

        if (!TypeTestUtil.isA(Comparable.class, node)) {
            return; // Only check Comparable classes
        }

        if (node instanceof ASTRecordDeclaration) {
            return; // Skip records, as equals/hashCode are auto-generated
        }

        ASTMethodDeclaration equalsMethod = null;
        ASTMethodDeclaration hashCodeMethod = null;

        for (ASTMethodDeclaration m : node.getDeclarations(ASTMethodDeclaration.class)) {
            if (JavaAstUtils.isEqualsMethod(m)) {
                equalsMethod = m;
            } else if (JavaAstUtils.isHashCodeMethod(m)) {
                hashCodeMethod = m;
            }
        }

        if (equalsMethod == null && hashCodeMethod == null) {
            asCtx(data).addViolationWithMessage(node, "missing equals and hashCode");
        } else if (equalsMethod == null) {
            asCtx(data).addViolationWithMessage(node, "missing equals");
        } else if (hashCodeMethod == null) {
            // Has equals but no hashCode
            asCtx(data).addViolation(equalsMethod);
        }
    }
}
