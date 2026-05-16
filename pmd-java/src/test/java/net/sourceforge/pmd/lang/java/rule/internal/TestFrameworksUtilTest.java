/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit4Class;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;

class TestFrameworksUtilTest {

    protected final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Test
    void testIsProbableAssertCallWithoutExtraMethodNames() {
        ASTCompilationUnit root = java.parse("class A { { assertThat(1); } }");
        ASTMethodCall m = root.descendants(ASTMethodCall.class).toList().get(0);
        assertThat(TestFrameworksUtil.isProbableAssertCall(m)).isTrue();
    }

    @Nested
    class IsJUnit4Class {
        @Test
        void aBasicClassIsNotAJUnit4Class() {
            ASTCompilationUnit root = java.parse("public class A {}");
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit4Class(classDecl));
        }

        @Test
        void aClassWithATestIsAJUnit4Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.Test; public class A { @Test public void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit4Class(classDecl));
        }

        @Test
        void aClassWithATestIsAJUnit4ClassEvenIfTheTestIsInAParentClass() {
            ASTCompilationUnit root = java.parse(
                    "import net.sourceforge.pmd.lang.java.rule.errorprone.rulesfortests.JUnit4ParentWithTest; public class A extends JUnit4ParentWithTest {}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit4Class(classDecl));
        }

        @Test
        void anInterfaceWithATestIsANotJUnit4Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.Test; public interface A { @Test public default void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit4Class(classDecl));
        }

        @Test
        void anAbstractClassWithATestIsANotJUnit4Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.Test; public abstract class A { @Test public void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit4Class(classDecl));
        }

        @Test
        void aNestedClassWithATestIsANotJUnit4Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.Test; class A { class B { @Test void foo() {} } }"
            );
            ASTClassDeclaration classDecl = root.descendants(ASTClassDeclaration.class).last();

            assertFalse(isJUnit4Class(classDecl));
        }
    }
}
