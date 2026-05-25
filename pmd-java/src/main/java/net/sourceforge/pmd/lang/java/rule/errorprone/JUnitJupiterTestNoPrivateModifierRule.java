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
public class JUnitJupiterTestNoPrivateModifierRule extends AbstractJavaRulechainRule {

    public JUnitJupiterTestNoPrivateModifierRule() {
        super(ASTClassDeclaration.class, ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isJUnit5Class(node) && node.hasVisibility(V_PRIVATE)) {
            ctx.addViolation(node);
        }

        return null;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isJUnit5Method(node) && node.hasVisibility(V_PRIVATE)) {
            ctx.addViolation(node);
        }

        return null;
    }
}
