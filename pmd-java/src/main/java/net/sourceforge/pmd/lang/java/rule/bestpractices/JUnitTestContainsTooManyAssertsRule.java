/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.NumericConstraints;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import java.util.List;

public class JUnitTestContainsTooManyAssertsRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> MAX_ASSERTS =
        PropertyFactory.intProperty("maximumAsserts")
                       .desc("Maximum number of assert calls in a test method")
                       .require(NumericConstraints.positive())
                       .defaultValue(1)
                       .build();

    private static final PropertyDescriptor<List<String>> EXTRA_ASSERT_METHOD_NAMES =
            PropertyFactory.stringListProperty("extraAssertMethodNames")
                           .desc("Extra valid assertion methods names")
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
            int assertCount = body.descendants(ASTMethodCall.class)
                                  .filter(call -> TestFrameworksUtil.isProbableAssertCall(call, getProperty(EXTRA_ASSERT_METHOD_NAMES)))
                                  .count();
            if (assertCount > getProperty(MAX_ASSERTS)) {
                asCtx(data).addViolation(method);
            }
        }
        return data;
    }
}
