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
import net.sourceforge.pmd.reporting.RuleContext;

public class OverrideBothEqualsAndHashcodeRule extends AbstractJavaRulechainRule {

    public OverrideBothEqualsAndHashcodeRule() {
        super(ASTClassDeclaration.class,
              ASTRecordDeclaration.class,
              ASTAnonymousClassDeclaration.class);
    }

    protected boolean skipType(ASTTypeDeclaration node) {
        return TypeTestUtil.isA(Comparable.class, node);
    }

    private void visitTypeDecl(ASTTypeDeclaration node, Object data) {
        if (skipType(node)) {
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

        maybeReport(asCtx(data), node, hashCodeMethod, equalsMethod);
    }

    protected void maybeReport(RuleContext ctx, ASTTypeDeclaration node, ASTMethodDeclaration hashCodeMethod, ASTMethodDeclaration equalsMethod) {
        if (hashCodeMethod != null ^ equalsMethod != null) {
            ASTMethodDeclaration nonNullNode =
                    equalsMethod == null ? hashCodeMethod : equalsMethod;
            ctx.addViolation(nonNullNode);
        }
    }

    @Override
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
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
