/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.rule.codestyle.UseDiamondOperatorRule.isSuperTypeTokenPattern;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.test.PmdRuleTst;

class UseDiamondOperatorTest extends PmdRuleTst {

    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Nested
    class IsSuperTypeTokenPattern {
        @Test
        @DisplayName("Creating an object of an anonymous class without a class body, where the parent class is generic - looks like the super type pattern")
        void testPositive() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> { private java.lang.reflect.Type type; } public class A { { new TypeReference<java.util.List<String>>() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertTrue(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Needs to create an anonymous class")
        void testNoAnonymousClass() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> { private java.lang.reflect.Type type; } public class A { { new TypeReference<java.util.List<String>>(); } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Anonymous class body needs to be empty")
        void testAnonymousClassBodyIsntEmpty() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> { private java.lang.reflect.Type type; } public class A { { new TypeReference<java.util.List<String>>() { void foo() {} }; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Superclass must be generic")
        void testSuperclassNotGeneric() {
            ASTCompilationUnit root = java.parse("class TypeReference { private java.lang.reflect.Type type; } public class A { { new TypeReference() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Missing type arguments")
        void testMissingTypeArguments() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> { private java.lang.reflect.Type type; } public class A { { new TypeReference() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Interfaces can't be type references")
        void testInterface() {
            ASTCompilationUnit root = java.parse("interface TypeReference<T> {} public class A { { new TypeReference<java.util.List<String>>() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Missing member of type java.lang.reflect.Type")
        void testTypeMember() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> {} public class A { { new TypeReference<java.util.List<String>>() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertFalse(isSuperTypeTokenPattern(ctorCall));
        }

        @Test
        @DisplayName("Type token has additional array member")
        void testArrayMember() {
            ASTCompilationUnit root = java.parse("class TypeReference<T> { private Object[] o; private java.lang.reflect.Type type; } public class A { { new TypeReference<java.util.List<String>>() {}; } }");
            ASTConstructorCall ctorCall = root.descendants(ASTConstructorCall.class).first();

            assertTrue(isSuperTypeTokenPattern(ctorCall));
        }
    }
}
