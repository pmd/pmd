/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Set<String>> EXTRA_ASSERT_METHOD_NAMES =
            PropertyFactory.stringProperty("extraAssertMethodNames")
                           .desc("Extra valid assertion methods names")
                           .map(Collectors.toSet())
                           .emptyDefaultValue()
                           .build();

    public JUnitTestsShouldIncludeAssertRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(EXTRA_ASSERT_METHOD_NAMES);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        Set<String> extraAsserts = getProperty(EXTRA_ASSERT_METHOD_NAMES);
        if (body != null
            && TestFrameworksUtil.isTestMethod(method)
            && !TestFrameworksUtil.isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class)
                   .none(call -> TestFrameworksUtil.isProbableAssertCall(call)
                           || extraAsserts.contains(call.getMethodName()))) {
            asCtx(data).addViolation(method);
        }
        return data;
    }
}
