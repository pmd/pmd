/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static java.lang.reflect.Modifier.isPublic;
import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PUBLIC;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.21.0 (as XPath) / 7.26.0 (as Java)
 */
public class PublicMemberInNonPublicTypeRule extends AbstractJavaRulechainRule {

    public PublicMemberInNonPublicTypeRule() {
        super(ASTMethodDeclaration.class,
                ASTConstructorDeclaration.class,
                ASTCompactConstructorDeclaration.class,
                ASTFieldDeclaration.class,
                ASTClassDeclaration.class,
                ASTEnumDeclaration.class,
                ASTAnnotationTypeDeclaration.class
        );
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getName());
        }

        return null;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getName());
        }

        return null;
    }

    @Override
    public Object visit(ASTCompactConstructorDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getEnclosingType().getSimpleName());
        }

        return null;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            for (ASTVariableId varId : node.getVarIds()) {
                ctx.addViolation(varId, varId.getName());
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getSimpleName());
        }

        return null;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getSimpleName());
        }

        return null;
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isViolation(node)) {
            ctx.addViolation(node, node.getSimpleName());
        }

        return null;
    }

    private boolean isViolation(ASTMethodDeclaration node) {
        return isViolation((ModifierOwner) node)
                // special case: if a method is overriding another method we only report
                // if we're allowed to reduce the visibility.
                && (!node.isOverride() || !isPublic(node.getOverriddenMethod().getModifiers()))
                // special case: always exclude main methods, they are allowed to be public
                && !node.isMainMethod();
    }

    private boolean isViolation(ModifierOwner node) {
        return node.getEffectiveVisibility() != V_PUBLIC && node.getVisibility() == V_PUBLIC
                && !node.ancestors(ASTTypeDeclaration.class).first().isInterface();
    }
}
