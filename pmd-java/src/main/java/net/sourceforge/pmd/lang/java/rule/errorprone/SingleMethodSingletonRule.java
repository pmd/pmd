/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;


import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Returns Checks if the singleton rule is used properly.
 */
public class SingleMethodSingletonRule extends AbstractJavaRulechainRule {

    public SingleMethodSingletonRule() {
        super(ASTClassDeclaration.class);
    }

    /**
     * Checks for getInstance method usage in the same class.
     * @param node of ASTCLass
     * @param data of Object
     * @return Object
     */
    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        int count = node.descendants(ASTMethodDeclaration.class)
            .filter(m -> "getInstance".equals(m.getName()))
            .count();
        if (count > 1) {
            ctx.addViolation(node);
        }
        return null;
    }
}
