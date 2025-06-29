/**
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
        if (!(node instanceof ASTClassDeclaration) || !node.isInterface() && !node.isAbstract()) {
            boolean isComparable = TypeTestUtil.isA(Comparable.class, node);
            ASTMethodDeclaration equalsMethod = null;
            ASTMethodDeclaration hashCodeMethod = null;

            for (ASTMethodDeclaration m : node.getDeclarations(ASTMethodDeclaration.class)) {
                if (JavaAstUtils.isEqualsMethod(m)) {
                    equalsMethod = m;
                    if (hashCodeMethod != null && !isComparable) {
                        break;
                    }
                } else if (JavaAstUtils.isHashCodeMethod(m)) {
                    hashCodeMethod = m;
                    if (equalsMethod != null && !isComparable) {
                        break;
                    }
                }
            }

            if (isComparable) {
                if (equalsMethod == null) {
                    asCtx(data).addViolation(node);
                } else if (hashCodeMethod == null) {
                    asCtx(data).addViolation(equalsMethod);
                }
            } else {
                if (hashCodeMethod != null ^ equalsMethod != null) {
                    asCtx(data).addViolation(equalsMethod == null ? hashCodeMethod : equalsMethod);
                }
            }
        }
    }
}
