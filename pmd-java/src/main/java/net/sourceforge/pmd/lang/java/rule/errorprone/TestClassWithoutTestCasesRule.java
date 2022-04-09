/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit3Class;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

public class TestClassWithoutTestCasesRule extends AbstractJavaRulechainRule {

    public TestClassWithoutTestCasesRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (isJUnit3Class(node)) {
            boolean hasTests =
                node.getDeclarations(ASTMethodDeclaration.class)
                    .any(TestFrameworksUtil::isJunit3MethodSignature);

            if (!hasTests) {
                addViolation(data, node);
            }
        }
        return null;
    }
}
