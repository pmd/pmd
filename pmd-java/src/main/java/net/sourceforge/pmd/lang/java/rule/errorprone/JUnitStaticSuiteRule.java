/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

public class JUnitStaticSuiteRule extends AbstractJUnitRule {

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getArity() != 0) {
            return super.visit(node, data);
        }
        String name = node.getName();
        if ("suite".equals(name) && (!node.isStatic() || !node.isPublic())) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}
