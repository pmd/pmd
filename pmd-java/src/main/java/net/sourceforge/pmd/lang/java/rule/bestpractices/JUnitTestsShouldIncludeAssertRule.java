/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JUnitRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJavaRulechainRule {


    public JUnitTestsShouldIncludeAssertRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        if (body != null
            && JUnitRuleUtil.isJUnitMethod(method)
            && !isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class)
                   .none(JUnitTestsShouldIncludeAssertRule::isProbableAssertCall)) {
            addViolation(data, method);
        }
        return data;
    }

    private static boolean isProbableAssertCall(ASTMethodCall call) {
        String name = call.getMethodName();
        return name.startsWith("assert") && !isSoftAssert(call)
            || name.startsWith("check")
            || name.startsWith("verify")
            || name.equals("fail")
            || name.equals("failWith")
            || JUnitRuleUtil.isExpectExceptionCall(call);
    }

    private static boolean isSoftAssert(ASTMethodCall call) {
        return TypeTestUtil.isA("org.assertj.core.api.AbstractSoftAssertions", call.getMethodType().getDeclaringType())
            && !"assertAll".equals(call.getMethodName());
    }

    /**
     * Tells if the node contains a Test annotation with an expected exception.
     */
    private boolean isExpectAnnotated(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations()
                     .filter(JUnitRuleUtil::isJunit4TestAnnotation)
                     .flatMap(ASTAnnotation::getMembers)
                     .any(it -> "expected".equals(it.getName()));

    }

}
