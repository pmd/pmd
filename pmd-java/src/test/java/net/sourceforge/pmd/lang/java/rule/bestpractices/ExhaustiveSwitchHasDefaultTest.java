/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.defaultBranchIsNecessary;
import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.defaultBranchJustThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    class DefaultBranchJustThrows {
        @Test
        @DisplayName("Classical switch with default that just throws => true")
        void testClassicalSwitchDefaultJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Classical switch with default that does more than just throwing => false;")
        void testClassicalSwitchDefaultMoreThanJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: i++; throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Classical switch with default that does not throw => false;")
        void testClassicalSwitchDefaultDoesNotThrow() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch(i) { default: break; } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that just throws => true")
        void testArrowSwitchDefaultJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> throw new IllegalStateException(); } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that just throws (in braces) => true")
        void testArrowSwitchDefaultJustThrowsInBraces() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { throw new IllegalStateException(); } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertTrue(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that does more than just throwing => false;")
        void testArrowSwitchDefaultMoreThanJustThrows() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { i++; throw new IllegalStateException(); } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that does not throw => false;")
        void testArrowSwitchDefaultDoesNotThrow() {
            ASTCompilationUnit root = java.parse("public class Foo { public void foo(int i) { switch (i) { default -> { i++; } } } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(defaultBranchJustThrows(defaultBranch));
        }

        @Test
        @DisplayName("Arrow style switch with default that returns a value => false;")
        void testArrowSwitchDefaultReturns() {
            ASTCompilationUnit root = java.parse("public class Foo { public int foo(int i) { return switch (i) { default -> 42; }; } }");
            ASTSwitchBranch defaultBranch = root.descendants(ASTSwitchBranch.class).first();

            assertFalse(defaultBranchJustThrows(defaultBranch));
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
}
