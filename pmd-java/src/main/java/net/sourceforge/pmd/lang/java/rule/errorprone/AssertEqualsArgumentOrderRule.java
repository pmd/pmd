/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;

/**
 * Looks for usages of assertEquals where the "actual" argument is a constant
 * and "expected" is not, indicated they were swapped.
 * @since 7.26.0
 */
public class AssertEqualsArgumentOrderRule extends AbstractJavaRulechainRule {

    public AssertEqualsArgumentOrderRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (!"assertEquals".equals(node.getMethodName())) {
            return null;
        }
        TestFrameworksUtil.EQUAL_METHODS.forEach(method -> {
            if (method.matches(node)) {
                ASTExpression actual = node.getArguments().get(method.actualPosition);
                if (actual.getConstFoldingResult().getValue() != null) {
                    ASTExpression expected = node.getArguments().get(method.expectedPosition);
                    if (expected.getConstFoldingResult().getValue() == null
                            || !(expected instanceof ASTLiteral) && actual instanceof ASTLiteral) {
                        asCtx(data).addViolation(node);
                    }
                }
            }
        });
        return null;
    }
}
