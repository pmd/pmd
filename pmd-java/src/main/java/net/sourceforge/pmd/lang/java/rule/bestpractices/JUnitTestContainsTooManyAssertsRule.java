/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;

public class JUnitTestContainsTooManyAssertsRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> MAX_ASSERTS =
        PropertyFactory.intProperty("maximumAsserts")
                       .desc("Maximum number of assert calls in a test method")
                       .require(NumericConstraints.positive())
                       .defaultValue(1)
                       .build();


    public JUnitTestContainsTooManyAssertsRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(MAX_ASSERTS);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        if (body != null && TestFrameworksUtil.isTestMethod(method)) {
            int assertCount = body.descendants(ASTMethodCall.class)
                                  .filter(TestFrameworksUtil::isProbableAssertCall)
                                  .count();
            if (assertCount > getProperty(MAX_ASSERTS)) {
                addViolation(data, method);
            }
        }
        return data;
    }
}
