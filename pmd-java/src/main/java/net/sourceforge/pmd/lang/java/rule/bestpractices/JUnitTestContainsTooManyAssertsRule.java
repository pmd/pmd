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
import net.sourceforge.pmd.properties.NumericConstraints;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class JUnitTestContainsTooManyAssertsRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> MAX_ASSERTS =
        PropertyFactory.intProperty("maximumAsserts")
                       .desc("Maximum number of assert calls in a test method")
                       .require(NumericConstraints.positive())
                       .defaultValue(1)
                       .build();

    private static final PropertyDescriptor<Set<String>> EXTRA_ASSERT_METHOD_NAMES =
            PropertyFactory.stringProperty("extraAssertMethodNames")
                           .desc("Extra valid assertion methods names")
                           .map(Collectors.toSet())
                           .emptyDefaultValue()
                           .build();


    public JUnitTestContainsTooManyAssertsRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(MAX_ASSERTS);
        definePropertyDescriptor(EXTRA_ASSERT_METHOD_NAMES);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        if (body != null && TestFrameworksUtil.isTestMethod(method)) {
            Set<String> extraAsserts = getProperty(EXTRA_ASSERT_METHOD_NAMES);
            int assertCount = body.descendants(ASTMethodCall.class)
                                  .filter(call -> TestFrameworksUtil.isProbableAssertCall(call)
                                  || extraAsserts.contains(call.getMethodName()))
                                  .count();
            if (assertCount > getProperty(MAX_ASSERTS)) {
                asCtx(data).addViolation(method);
            }
        }
        return data;
    }
}
