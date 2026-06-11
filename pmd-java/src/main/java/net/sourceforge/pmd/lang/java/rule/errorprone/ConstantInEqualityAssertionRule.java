/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 * Looks for usages of assertEquals where the "actual" argument is a constant.
 * @since 7.26.0
 */
public class ConstantInEqualityAssertionRule extends AbstractJavaRulechainRule {

    private static final class EqualMethod {
        private final InvocationMatcher matcher;
        private final int actualPosition;
        private final int expectedPosition;

        private EqualMethod(String pattern, int actualPosition, int expectedPosition) {
            this.matcher = InvocationMatcher.parse(pattern);
            this.expectedPosition = expectedPosition;
            this.actualPosition = actualPosition;
        }
    }

    private static final List<EqualMethod> EQUAL_METHODS = Arrays.asList(
        // JUnit Jupiter: expected, actual, [message]
        new EqualMethod("org.junit.jupiter.api.Assertions#assertEquals(_,_)", 1, 0),
        new EqualMethod("org.junit.jupiter.api.Assertions#assertEquals(_,_,_)", 1, 0),
        // JUnit 3 and 4, Spring: [message], expected, actual
        new EqualMethod("org.junit.Assert#assertEquals(_,_)", 1, 0),
        new EqualMethod("org.junit.Assert#assertEquals(_,_,_)", 2, 1),
        new EqualMethod("junit.framework.TestCase#assertEquals(_,_)", 1, 0),
        new EqualMethod("junit.framework.TestCase#assertEquals(_,_,_)", 2, 1),
        new EqualMethod("org.springframework.test.util.AssertionErrors#assertEquals(_,_,_)", 2, 1),
        // TestNG: actual, expected, [message]
        new EqualMethod("org.testng.Assert#assertEquals(_*)", 0, 1),
        // JSONAssert: [message], expected, actual, compare mode
        new EqualMethod("org.skyscreamer.jsonassert.JSONAssert#assertEquals(_,_,_)", 1, 0),
        new EqualMethod("org.skyscreamer.jsonassert.JSONAssert#assertEquals(_,_,_,_)", 2, 1)
    );

    public ConstantInEqualityAssertionRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (!"assertEquals".equals(node.getMethodName())) {
            return null;
        }
        EQUAL_METHODS.forEach(method -> {
            if (method.matcher.matchesCall(node)) {
                ASTExpression actual = node.getArguments().get(method.actualPosition);
                if (actual.getConstFoldingResult().getValue() != null) {
                    if (actual instanceof ASTLiteral
                            || node.getArguments().get(method.expectedPosition)
                                    .getConstFoldingResult().getValue() == null) {
                        asCtx(data).addViolation(node);
                    }
                }
            }
        });
        return null;
    }
}
