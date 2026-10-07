/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.errorprone.UnusedReturnValueRule.isCheckReturnValueAnnotated;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.test.PmdRuleTst;

class UnusedReturnValueTest extends PmdRuleTst {

    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Nested
    class IsCheckReturnValueAnnotated {
        @Test
        @DisplayName("If a method is not annotated with @CheckReturnValue, the return value doesn't need to be checked")
        void notAnnotatedMethod() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.HasAnnotatedMethod.notAnnotatedMethod(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is annotated with @CheckReturnValue, the return value should be checked")
        void annotatedMethod() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.HasAnnotatedMethod.annotatedMethod(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertTrue(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class annotated with @CheckReturnValue, the return value should be checked")
        void annotatedClass() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.AnnotatedClass.shouldBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertTrue(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class annotated with @CheckReturnValue but the method is annotated with @CanIgnoreReturnValue, the return value doesn't need to be checked")
        void annotatedClassMethodShouldNotBeChecked() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.AnnotatedClass.doesNotNeedToBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class annotated with @CheckReturnValue but the method returns void, the return value doesn't need to be checked")
        void returningVoid() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.AnnotatedClass.returnsVoid(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class in a package annotated with @CheckReturnValue, the return value should be checked")
        void annotatedPackage() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage.ClassInAnnotatedPackage.shouldBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertTrue(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class in a package annotated with @CheckReturnValue but the method is annotated with @CanIgnoreReturnValue, the return value doesn't need to be checked")
        void annotatedPackageMethodShouldNotBeChecked() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage.ClassInAnnotatedPackage.doesNotNeedToBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("If a method is in a class in a package annotated with @CheckReturnValue but the class is annotated with @CanIgnoreReturnValue, the return value doesn't need to be checked")
        void annotatedPackageClassShouldNotBeChecked() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage.ClassWithCanIgnoreReturnValue.doesNotNeedToBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("Package, class and method are annotated (alternating)")
        void annotatedPackageClassAndMethod() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage.ClassWithCanIgnoreReturnValue.shouldBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertTrue(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("A package with an unrelated annotation")
        void packageWithUnrelatedAnnotation() {
            ASTCompilationUnit root = java.parse("public class Foo { { net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.packagewithadifferentannotation.SomeClass.doesNotNeedToBeChecked(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }

        @Test
        @DisplayName("Calling an unresolved method shouldn't crash the rule")
        void unresolvedMethod() {
            ASTCompilationUnit root = java.parse("import a.b.Foo; public class Foo { { Foo.bar(); } }");
            ASTMethodCall methodCall = root.descendants(ASTMethodCall.class).first();

            assertFalse(isCheckReturnValueAnnotated(methodCall));
        }
    }
}
