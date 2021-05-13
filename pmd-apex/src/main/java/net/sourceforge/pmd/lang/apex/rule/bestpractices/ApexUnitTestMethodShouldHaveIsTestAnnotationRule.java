/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.TEST_METHOD;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class ApexUnitTestMethodShouldHaveIsTestAnnotationRule extends AbstractApexUnitTestRule {

    @Override
    public Object visit(final ASTMethod node, final Object data) {
        // test methods should have @isTest annotation not testMethod
        if (isTestMethodOrClass(node)) {
            if (hasDeprecatedTestMethodAnnotation(node)) {
                return addViolation(node, data);
            }
        }
        return data;
    }

    private boolean hasDeprecatedTestMethodAnnotation(final ASTMethod method) {
        return method.getNode().getModifiers().has(TEST_METHOD);
    }

    private Object addViolation(final ASTMethod testMethod, final Object data) {
        addViolationWithMessage(
            data,
            testMethod,
            "''{0}'' method should have @isTest annotation.",
            new Object[] { testMethod.getImage() }
        );
        return data;
    }
}
