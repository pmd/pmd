/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

public class JUnitSpellingRule extends AbstractJavaRulechainRule {

    public JUnitSpellingRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (TestFrameworksUtil.isJUnit3Class(node)) {
            node.getDeclarations(ASTMethodDeclaration.class)
                .filter(this::isViolation)
                .forEach(it -> asCtx(data).addViolation(it));
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
