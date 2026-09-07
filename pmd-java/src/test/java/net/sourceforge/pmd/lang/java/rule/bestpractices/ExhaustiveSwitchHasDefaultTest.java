/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.branchJustThrows;
import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.defaultBranchIsNecessary;
import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.formatMissingCases;
import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.missingCases;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.test.PmdRuleTst;

class ExhaustiveSwitchHasDefaultTest extends PmdRuleTst {

    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Nested
    class BranchJustThrows {
        @Test
        @DisplayName("Classical switch with default that just throws => true")
        void testClassicalSwitchDefaultJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Classical switch with default that does more than just throwing => false")
        void testClassicalSwitchDefaultMoreThanJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: i++; throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Classical switch with default that does not throw => false")
        void testClassicalSwitchDefaultDoesNotThrow() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: break; } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that just throws => true")
        void testArrowSwitchDefaultJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that just throws (in braces) => true")
        void testArrowSwitchDefaultJustThrowsInBraces() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { throw new IllegalStateException(); } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that does more than just throwing => false")
        void testArrowSwitchDefaultMoreThanJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { i++; throw new IllegalStateException(); } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that does not throw => false")
        void testArrowSwitchDefaultDoesNotThrow() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { i++; } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(branchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that returns a value => false")
        void testArrowSwitchDefaultReturns() {
            ASTCompilationUnit root = java.parse("public class Foo { public int foo(int i) { return switch (i) { default -> 42; }; } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(branchJustThrows(defaultBranch));
        }
    }

    @Nested
    class DefaultBranchIsNecessary {
        @Test
        @DisplayName("Default branch is necessary, because without it, the compiler will complaint that foo might not have been initialized.")
        void testPositive() {
            ASTCompilationUnit root = java.parse("public class Foo { private final int foo; public Foo(int i) { switch(i) { case 1: foo = 1; break; default: throw new IllegalArgumentException(); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchStatement.class).first();

            assertTrue(defaultBranchIsNecessary(switchLike));
        }

        @Test
        @DisplayName("Variable isn't final, doesn't have to be initialized")
        void testVarIsntFinal() {
            ASTCompilationUnit root = java.parse("public class Foo { private int foo; public Foo(int i) { switch(i) { case 1: foo = 1; break; default: throw new IllegalArgumentException(); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchStatement.class).first();

            assertFalse(defaultBranchIsNecessary(switchLike));
        }

        @Test
        @DisplayName("Final var is only read")
        void testReadFinalVar() {
            ASTCompilationUnit root = java.parse("public class Foo { private int foo; private final int bar = 42; public Foo(int i) { switch(i) { case 1: foo = bar; break; default: throw new IllegalArgumentException(); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchStatement.class).first();

            assertFalse(defaultBranchIsNecessary(switchLike));
        }

        @Test
        @DisplayName("No referenced variable")
        void testNoVarReferenced() {
            ASTCompilationUnit root = java.parse("public class Foo { private int foo; public Foo(int i) { switch(i) { default: throw new IllegalArgumentException(); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchStatement.class).first();

            assertFalse(defaultBranchIsNecessary(switchLike));
        }
    }

    @Nested
    class MissingCases {
        @Test
        @DisplayName("Enum switch lists constants only handled by default")
        void testEnumMissingConstant() {
            ASTCompilationUnit root = java.parse("public class Foo { enum State { NEW, ACTIVE, DONE, VOID } static String apply(State state) { return switch (state) { case ACTIVE -> \"active\"; case DONE -> \"done\"; case VOID -> \"void\"; default -> \"x\"; }; } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchLike.class).first();

            assertEquals(Collections.singletonList("NEW"), missingCases(switchLike));
        }

        @Test
        @DisplayName("Multiple missing enum constants are sorted")
        void testEnumMissingConstantsAreSorted() {
            ASTCompilationUnit root = java.parse("public class Foo { enum State { NEW, ACTIVE, DONE, VOID } void apply(State state) { switch (state) { case ACTIVE -> System.out.println(\"a\"); default -> System.out.println(\"x\"); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchLike.class).first();

            assertEquals(Arrays.asList("DONE", "NEW", "VOID"), missingCases(switchLike));
        }

        @Test
        @DisplayName("Sealed type switch lists uncovered permitted subtypes")
        void testSealedMissingSubtype() {
            ASTCompilationUnit root = java.parse("sealed interface Foo { final class A implements Foo {} record B() implements Foo {} default void doSomething(Foo foo) { switch (foo) { case A a -> System.out.println(\"a\"); default -> System.out.println(\"x\"); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchLike.class).first();

            assertEquals(Collections.singletonList("B"), missingCases(switchLike));
        }

        @Test
        @DisplayName("Fully covered enum has no missing cases")
        void testAllCovered() {
            ASTCompilationUnit root = java.parse("public class Foo { enum MyEnum { A, B } void doSomething(MyEnum e) { switch(e) { case A -> System.out.println(\"a\"); case B -> System.out.println(\"b\"); default -> System.out.println(\"x\"); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchLike.class).first();

            assertEquals(Collections.emptyList(), missingCases(switchLike));
        }

        @Test
        @DisplayName("Non-enum, non-sealed selector has no named missing cases")
        void testNonClassSelector() {
            ASTCompilationUnit root = java.parse("public class Foo { void doSomething(int i) { switch(i) { case 1 -> System.out.println(\"a\"); default -> System.out.println(\"x\"); } } }");
            ASTSwitchLike switchLike = root.descendants(ASTSwitchLike.class).first();

            assertEquals(Collections.emptyList(), missingCases(switchLike));
        }
    }

    @Nested
    class FormatMissingCases {
        @Test
        void emptyListOmitsParentheses() {
            assertEquals("", formatMissingCases(Collections.emptyList()));
        }

        @Test
        void singleName() {
            assertEquals(" (NEW)", formatMissingCases(Collections.singletonList("NEW")));
        }

        @Test
        void truncatesAfterThreeNames() {
            assertEquals(" (A, B, C, ...)", formatMissingCases(Arrays.asList("A", "B", "C", "D", "E")));
        }
    }
}
