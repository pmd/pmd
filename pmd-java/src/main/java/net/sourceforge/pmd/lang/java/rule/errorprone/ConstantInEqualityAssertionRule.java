/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 * Looks for usages of assertEquals where the "actual" argument is a constant.
 */
public class ConstantInEqualityAssertionRule extends AbstractJavaRulechainRule {

    private static final class EqualMethod {
        InvocationMatcher matcher;
        int actualPosition;

        private EqualMethod(String pattern, int actualPosition) {
            this.matcher = InvocationMatcher.parse(pattern);
            this.actualPosition = actualPosition;
        }
    }

    private static final List<EqualMethod> EQUAL_METHODS = Arrays.asList(
        new EqualMethod("org.junit.Assert#assertEquals(_,_)", 1),
        new EqualMethod("org.junit.Assert#assertEquals(_,_,_)", 2),
        new EqualMethod("junit.framework.TestCase#assertEquals(_,_)", 1),
        new EqualMethod("junit.framework.TestCase#assertEquals(_,_,_)", 2),
        new EqualMethod("org.junit.jupiter.api.Assertions#assertEquals(_,_)", 1),
        new EqualMethod("org.junit.jupiter.api.Assertions#assertEquals(_,_,_)", 1)
    );

    public ConstantInEqualityAssertionRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        EQUAL_METHODS.forEach(method -> {
            if (method.matcher.matchesCall(node)) {
                if (node.getArguments().get(method.actualPosition) instanceof ASTLiteral) {
                    asCtx(data).addViolation(node);
                }
            }
        });
        return null;
    }
}
