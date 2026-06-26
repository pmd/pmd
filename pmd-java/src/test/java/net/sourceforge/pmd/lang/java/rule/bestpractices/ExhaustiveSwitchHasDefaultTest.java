/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.rule.bestpractices.ExhaustiveSwitchHasDefaultRule.defaultBranchJustThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
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
}
