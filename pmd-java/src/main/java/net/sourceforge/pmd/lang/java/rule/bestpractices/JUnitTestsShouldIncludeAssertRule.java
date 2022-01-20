/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJavaRulechainRule {


    public JUnitTestsShouldIncludeAssertRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        if (body != null
            && TestFrameworksUtil.isJUnitMethod(method)
            && !TestFrameworksUtil.isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class)
                   .none(TestFrameworksUtil::isProbableAssertCall)) {
            addViolation(data, method);
        }
        return super.visit(method, data);
    }
}
