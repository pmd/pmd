/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.containsCamelCaseWord;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isNullCheck;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isUtilityClass;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.startsWithCamelCaseWord;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

class JavaRuleUtilTest extends BaseParserTest {

    @Test
    void testCamelCaseWords() {
        assertFalse(startsWithCamelCaseWord("getter", "get"), "no word boundary");
        assertFalse(startsWithCamelCaseWord("get", "get"), "no following word");
        assertTrue(startsWithCamelCaseWord("getX", "get"), "ok prefix");
        assertFalse(startsWithCamelCaseWord("ge", "get"), "shorter word");

        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord("fnei", null));
    }

    @Test
    void testContainsCamelCaseWords() {

        assertFalse(containsCamelCaseWord("isABoolean", "Bool"), "no word boundary");
        assertTrue(containsCamelCaseWord("isABoolean", "A"), "ok word in the middle");
        assertTrue(containsCamelCaseWord("isABoolean", "Boolean"), "ok word at the end");

        assertThrows(NullPointerException.class, () -> containsCamelCaseWord(null, "A"));
        assertThrows(NullPointerException.class, () -> containsCamelCaseWord("fnei", null));
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", ""), "empty string");
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", "a"), "not capitalized");
    }

    @Nested
    class IsUtilityClass {

        @Test
        @DisplayName("a class with only static members is a utility class")
        void testPositive() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static String privateStaticMemberVariable = \"foo\";\n"
                            + "    public static String staticMemberVariable = \"foo\";\n"
                            + "    private static void privateStaticMemberFunction() {};\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "    private static class PrivateStaticNestedClass {};\n"
                            + "    public static class StaticNestedClass {};\n"
                            + "    static {}\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only static member variables is a utility class")
        void testStaticMemberVariables() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static String privateStaticMemberVariable = \"foo\";\n"
                            + "    public static String staticMemberVariable = \"foo\";\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with a non-static member variable is NOT a utility class")
        void testMemberVariable() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static String privateStaticMemberVariable = \"foo\";\n"
                            + "    public static String staticMemberVariable = \"foo\";\n"
                            + "    private String instanceVariable = \"bar\";\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only static member functions is a utility class")
        void testStaticMemberFunctions() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static void privateStaticMemberFunction() {};\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with a non-static member function is NOT a utility class")
        void testMemberFunction() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static void privateStaticMemberFunction() {};\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "    public void memberFunction() {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only static member classes is a utility class")
        void testStaticMemberClasses() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static class PrivateStaticNestedClass {};\n"
                            + "    public static class StaticNestedClass {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with a non-static member class is NOT a utility class")
        void testMemberClass() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static class PrivateStaticNestedClass {};\n"
                            + "    public static class StaticNestedClass {};\n"
                            + "    class MemberClass {};"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only a nested interface is a utility class - nested interfaces are implicitly static")
        void testNestedInterface() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    public interface NestedInterface {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only a nested enum is a utility class - nested enums are implicitly static")
        void testNestedEnum() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    public enum NestedEnum { A };\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with only a nested record is a utility class - nested records are implicitly static")
        void testNestedRecord() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    public record NestedRecord() {}\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with a non-static initializers is NOT a utility class")
        void testNonStaticInitializer() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    private static void privateStaticMemberFunction() {};\n"
                            + "    {}\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("an empty class is NOT a utility class")
        void testEmpty() {
            ASTCompilationUnit root = java.parse(
                    "public class A {}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("an interface is NOT a utility class")
        void testInterface() {
            ASTCompilationUnit root = java.parse(
                    "public interface A {\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("an abstract class is NOT a utility class")
        void testAbstractClass() {
            ASTCompilationUnit root = java.parse(
                    "public abstract class A {\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }

        @Test
        @DisplayName("a class with a main method is NOT a utility class")
        void testMainMethod() {
            ASTCompilationUnit root = java.parse(
                    "public class A {\n"
                            + "    public static void main(String... args) {};\n"
                            + "    public static void staticMemberFunction() {};\n"
                            + "}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isUtilityClass(classDecl));
        }
    }

    @Nested
    class OverloadedIsNullCheckTest {
        @Test
        @DisplayName("Returns true when variable is compared to null literal in overloaded method")
        void testStandardNullComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Object x) {"
                    + "if (x == null) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, xSymbol));
        }

        @Test
        @DisplayName("Returns false when variable is compared to non-null literal in overloaded method")
        void testStandardIntegerComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Integer x) {"
                    + "if (x == 1) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertFalse(isNullCheck(condition, xSymbol));
        }
    }

    @Nested
    class IsNullCheckTest {

        @Test
        @DisplayName("Returns true when variable is compared to null literal")
        void testStandardNullComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Object x) {"
                    + "if (x == null) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns true when variable is compared to null literal as (null == variable)")
        void testYodaNullComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Object x) {"
                    + "if (null == x) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns true when variable is compared as not equal to null literal")
        void testNotEqualNullComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Object x) {"
                    + "if (x != null) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns false when variable is compared directly to non-null literal")
        void testStandardIntegerComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Integer x) {"
                    + "if (x == 1) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertFalse(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns true when variable is compared to null literal inside a negation")
        void testWrappedNullComparison() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Object x) {"
                    + "if (!(!(x == null))) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns false when condition contains non-negation unary operator")
        void testNonNegationUnaryOperator() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Integer x) {"
                    + "x++;}}");
            ASTExpression unaryExpr = acu.descendants(ASTUnaryExpression.class).firstOrThrow();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertFalse(isNullCheck(unaryExpr, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns true when variable is compared to null literal inside redundant parentheses")
        void testRedundantParentheses() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Integer x) {"
                    + "if (((x == null))) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }

        @Test
        @DisplayName("Returns true when variable is compared to null literal inside redundant parentheses and negations")
        void testRedundantParenthesesWithNegations() {
            ASTCompilationUnit acu = java.parse("class Foo {"
                    + "void Bar(Integer x) {"
                    + "if (((!((!(x == null)))))) {}}}");
            ASTExpression condition = acu.descendants(ASTIfStatement.class).firstOrThrow().getCondition();
            JVariableSymbol xSymbol = acu.descendants(ASTVariableId.class).firstOrThrow().getSymbol();

            assertTrue(isNullCheck(condition, StablePathMatcher.matching(xSymbol)));
        }
    }
}
