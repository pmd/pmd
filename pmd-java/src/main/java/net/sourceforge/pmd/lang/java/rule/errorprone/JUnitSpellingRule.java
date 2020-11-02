/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule.JUNIT3_CLASS_NAME;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class JUnitSpellingRule extends AbstractJavaRulechainRule {

    public JUnitSpellingRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isRegularClass() && TypeTestUtil.isA(JUNIT3_CLASS_NAME, node)) {
            node.getDeclarations()
                .filterIs(ASTMethodDeclaration.class)
                .filter(this::isViolation)
                .forEach(it -> addViolation(data, it));
        }
        return null;
    }

    private boolean isViolation(ASTMethodDeclaration method) {
        if (method.getArity() != 0) {
            return false;
        }
        String name = method.getName();
        return !"setUp".equals(name) && "setup".equalsIgnoreCase(name)
            || !"tearDown".equals(name) && "teardown".equalsIgnoreCase(name);

    }
}
