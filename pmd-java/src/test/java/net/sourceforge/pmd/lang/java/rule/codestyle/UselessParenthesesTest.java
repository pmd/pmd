/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity;
import net.sourceforge.pmd.testframework.PmdRuleTst;

class UselessParenthesesTest extends PmdRuleTst {

    Executable testImpl(String expression, Necessity necessity) {
        return () -> {
            String file = "class Foo {{ int a,b,c,d; float f1, f2, f3; String s; Object e = " + expression + ";}}";
            ASTCompilationUnit acu = JavaParsingHelper.DEFAULT.parse(file);

            ASTExpression paren = acu.descendants(ASTExpression.class).crossFindBoundaries().first(ASTExpression::isParenthesized);

            assertNotNull(paren, "No parenthesized expression in " + expression);

            UselessParenthesesRule.Necessity result = UselessParenthesesRule.needsParentheses(paren, paren.getParent());

            assertEquals(necessity, result, "In " + expression);
        };
    }

    Executable clarifying(String expression) {
        return testImpl(expression, Necessity.CLARIFYING);
    }

    Executable balancing(String expression) {
        return testImpl(expression, Necessity.BALANCING);
    }

    Executable necessary(String expression) {
        return testImpl(expression, Necessity.ALWAYS);
    }

    Executable unnecessary(String expression) {
        return testImpl(expression, Necessity.NEVER);
    }

    @Test
    void testOuterLambdas() {
        assertAll(
            unnecessary("() -> (a + b)"),
            unnecessary("() -> (() -> b)"),
            unnecessary("() -> (a ? b : c)"), // clarifying?
            necessary("a ? () -> (a ? b : c) : d")
        );
    }

    @Test
    void testInnerLambda() {
        assertAll(
            necessary("(() -> 1) + 2"),
            unnecessary("((() -> 1)) + 2"),
            necessary("(() -> 1) * 2"),
            // necessary("(() -> 1) = 2"), (impossible)
            unnecessary("a = (() -> 1)")
        );
    }

    @Test
    void testAssignments() {
        //  (a = b) = c          (impossible)

        assertAll(
            necessary("a * (b = c)"),
            unnecessary("a * ((b = c))"),
            necessary("a ? (b = c) : d"),
            unnecessary("a = (b = c)")
        );
    }

    @Test
    void testConditionals() {
        assertAll(
            unnecessary("a ? b : (c ? d : e)"),

            necessary("a ? (b ? c : d) : e"),
            unnecessary("a ? ((b ? c : d)) : e"),
            necessary("(a ? b : c) ? d : e"),

            clarifying("(a == b) ? c : d"),
            unnecessary("(s.toString()) ? c : d")
        );
    }

    @Test
    void testAdditiveMul() {
        // remember, a,b,c,d are ints
        // fp1,2,3 are floats
        assertAll(
            unnecessary("a + (b + c)"),
            unnecessary("a + (b - c)"),
            clarifying("a + (b * c)"),

            unnecessary("(a + b) + c"),
            unnecessary("(f1 + f1) + f2"),
            unnecessary("(a + b) - c"),

            necessary("(a + b) * c"),
            necessary("(a + b) / c"),
            necessary("x / (a + d)"),


            // those mix floating-point operations
            necessary("a + (f1 + b)"),
            necessary("a + (f1 - b)"),

            necessary("a + (b + f1)"),
            necessary("a + (b - f1)"),

            necessary("f1 + (f1 + b)"),
            necessary("f1 + (f1 - b)"),

            necessary("f1 + (b + f1)"),
            necessary("f1 + (b - f1)")
        );
    }

    @Test
    void testMultiplicative() {
        assertAll(
            unnecessary("(a * b) * c"),

            unnecessary("a * (b * c)"),

            necessary("a * (b / c)"),
            necessary("a * (b % c)"),
            necessary("a / (b * c)"),
            necessary("a / (b / c)"),
            necessary("a / (b % c)"),
            necessary("a % (b * c)"),
            necessary("a % (b / c)"),
            necessary("a % (b % c)")
        );
    }

    @Test
    void testConcatenation() {
        assertAll(
            necessary("\"\" + (1 + 4)"),
            unnecessary("\"\" + (\"\" + 4)"),
            unnecessary("\"\" + (4 + \"\")"),
            clarifying("(1 + 4) + \"\"")
        );
    }

    @Test
    void testEquality() {
        assertAll(
            necessary("a == null == (b == null)"),
            unnecessary("a == null == ((b == null))"),
            balancing("(a == null) == (b == null)"),
            unnecessary("(a == true) == b == true"),
            // the same with some !=
            necessary("a == null != (b == null)"),
            unnecessary("a == null != ((b == null))"),
            balancing("(a == null) == (b != null)"),
            unnecessary("(a != true) == b == true")
        );
    }

    @Test
    void testRelational() {
        assertAll(
            clarifying("a <= b == (b <= c)"),
            clarifying("(a <= b) == (b <= c)"),
            necessary("(a == b) >= 0"),
            clarifying("(a + b) >= 0")
        );
    }

    @Test
    void testUnaries() {
        assertAll(
            unnecessary("(String) ((String) c)"),
            unnecessary("(String) (+1)"),
            unnecessary("a + +((char) 1)"),
            unnecessary("a + ~((char) 1)"),

            clarifying("a + ((char) 1)"),
            clarifying("a * ((char) 1)"),
            clarifying("((char) 1) * a"),

            unnecessary("-(-1)"),
            unnecessary("-(+1)")
        );
    }

    @Test
    void testSwitches() {
        assertAll(
            unnecessary(" a + (switch (1) { })"),
            unnecessary("(switch (1) { }) + 1")
        );
    }
}
