/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit4Class;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit5Class;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isTestNGClass;
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
                    "import org.junit.Test; public class A { class B { @Test public void foo() {} } }"
            );
            ASTClassDeclaration classDecl = root.descendants(ASTClassDeclaration.class).last();

            assertFalse(isJUnit4Class(classDecl));
        }
    }

    @Nested
    class IsJUnit5Class {
        @Test
        void aBasicClassIsNotAJUnit5Class() {
            ASTCompilationUnit root = java.parse("class A {}");
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithATestIsAJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.Test; class A { @Test void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithARepeatedTestIsAJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.RepeatedTest; class A { @RepeatedTest(10) void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithATestFactoryIsAJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.TestFactory; class A { @TestFactory Collection<DynamicTest> foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithATestTemplateIsAJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.TestTemplate; class A { @TestTemplate void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithAParameterizedTestIsAJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.params.ParameterizedTest; class A { @ParameterizedTest void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void aClassWithATestIsAJUnit5ClassEvenIfTheTestIsInAParentClass() {
            ASTCompilationUnit root = java.parse(
                    "import net.sourceforge.pmd.lang.java.rule.errorprone.rulesfortests.Junit5ParentWithTest; class A extends Junit5ParentWithTest {}"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isJUnit5Class(classDecl));
        }

        @Test
        void anInterfaceWithATestIsANotJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.Test; interface A { @Test default void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit5Class(classDecl));
        }

        @Test
        void anAbstractClassWithATestIsANotJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.Test; abstract class A { @Test void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isJUnit5Class(classDecl));
        }

        @Test
        void aNestedClassWithATestIsANotJUnit5Class() {
            ASTCompilationUnit root = java.parse(
                    "import org.junit.jupiter.api.Test; class A { class B { @Test void foo() {} } }"
            );
            ASTClassDeclaration classDecl = root.descendants(ASTClassDeclaration.class).last();

            assertFalse(isJUnit5Class(classDecl));
        }
    }

    @Nested
    class IsTestNGClass {
        @Test
        void aBasicClassIsNotATestNGClass() {
            ASTCompilationUnit root = java.parse("public class A {}");
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isTestNGClass(classDecl));
        }

        @Test
        void aClassWithATestIsATestNGClass() {
            ASTCompilationUnit root = java.parse(
                    "import org.testng.annotations.Test; public class A { @Test public void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertTrue(isTestNGClass(classDecl));
        }

        @Test
        void anInterfaceWithATestIsANotTestNGClass() {
            ASTCompilationUnit root = java.parse(
                    "import org.testng.annotations.Test; public interface A { @Test public default void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isTestNGClass(classDecl));
        }

        @Test
        void anAbstractClassWithATestIsANotTestNGClass() {
            ASTCompilationUnit root = java.parse(
                    "import org.testng.annotations.Test; public abstract class A { @Test public void foo() {} }"
            );
            ASTClassDeclaration classDecl = root.firstChild(ASTClassDeclaration.class);

            assertFalse(isTestNGClass(classDecl));
        }

        @Test
        void aNestedClassWithATestIsANotTestNGClass() {
            ASTCompilationUnit root = java.parse(
                    "import org.testng.annotations.Test; class A { class B { @Test public void foo() {} } }"
            );
            ASTClassDeclaration classDecl = root.descendants(ASTClassDeclaration.class).last();

            assertFalse(isTestNGClass(classDecl));
        }
    }

}
