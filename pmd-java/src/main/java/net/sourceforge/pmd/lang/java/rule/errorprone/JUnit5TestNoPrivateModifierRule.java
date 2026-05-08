/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PRIVATE;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit5Class;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit5Method;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.25.0
 */
public class JUnit5TestNoPrivateModifierRule extends AbstractJavaRulechainRule {

    public JUnit5TestNoPrivateModifierRule() {
        super(ASTClassDeclaration.class, ASTMethodDeclaration.class);
    }

    @Override
    public RuleContext visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isJUnit5Class(node.getTypeMirror()) && node.hasVisibility(V_PRIVATE)) {
            ctx.addViolation(node);
        }

        return ctx;
    }

    @Override
    public RuleContext visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isJUnit5Method(node) && node.hasVisibility(V_PRIVATE)) {
            ctx.addViolation(node);
        }

        return ctx;
    }
}
