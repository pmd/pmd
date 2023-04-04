/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Utilities for rules related to test frameworks (Junit, TestNG, etc).
 */
public final class TestFrameworksUtil {

    private static final String JUNIT3_CLASS_NAME = "junit.framework.TestCase";
    private static final String JUNIT4_TEST_ANNOT = "org.junit.Test";

    private static final String TESTNG_TEST_ANNOT = "org.testng.annotations.Test";

    private static final Set<String> JUNIT5_ALL_TEST_ANNOTS =
        setOf("org.junit.jupiter.api.Test",
              "org.junit.jupiter.api.RepeatedTest",
              "org.junit.jupiter.api.TestFactory",
              "org.junit.jupiter.api.TestTemplate",
              "org.junit.jupiter.params.ParameterizedTest"
        );

    private static final String JUNIT5_NESTED = "org.junit.jupiter.api.Nested";

    private static final Set<String> ASSERT_CONTAINERS = setOf("org.junit.Assert",
                                                               "org.junit.jupiter.api.Assertions",
                                                               "org.hamcrest.MatcherAssert",
                                                               "org.testng.Assert",
                                                               "junit.framework.Assert",
                                                               "junit.framework.TestCase");

    private TestFrameworksUtil() {
        // utility class
    }

    /**
     * True if this is a junit @Test method (or a junit 3 method).
     */
    public static boolean isJUnitMethod(ASTMethodDeclaration method) {
        if (method.hasModifiers(JModifier.STATIC) || method.getBody() == null) {
            return false; // skip various inapplicable method variations
        }

        boolean result = isJUnit5Method(method);
        result = result || isJUnit4Method(method);
        result = result || isJUnit3Method(method);
        return result;
    }

    /**
     * Returns true if this is either a JUnit test or a TestNG test.
     */
    public static boolean isTestMethod(ASTMethodDeclaration method) {
        return isJUnitMethod(method) || isTestNgMethod(method);
    }

    /**
     * Returns true if this is a Before/setUp method or After/tearDown.
     */
    public static boolean isTestConfigurationMethod(ASTMethodDeclaration method) {
        return method.isAnnotationPresent("org.junit.Before")
                || method.isAnnotationPresent("org.junit.BeforeClass")
                || method.isAnnotationPresent("org.junit.After")
                || method.isAnnotationPresent("org.junit.AfterClass")
                || isJUnit3Class(method.getEnclosingType())
                        && ("setUp".equals(method.getName())
                            || "tearDown".equals(method.getName()));
    }

    private static boolean isTestNgMethod(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(TESTNG_TEST_ANNOT);
    }

    public static boolean isJUnit4Method(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(JUNIT4_TEST_ANNOT)
                && method.getVisibility() == Visibility.V_PUBLIC;
    }

    public static boolean isJUnit5Method(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations().any(
            it -> {
                String canonicalName = it.getTypeMirror().getSymbol().getCanonicalName();
                return JUNIT5_ALL_TEST_ANNOTS.contains(canonicalName);
            }
        );
    }

    public static boolean isJUnit3Method(ASTMethodDeclaration method) {
        return isJUnit3Class(method.getEnclosingType())
            && isJunit3MethodSignature(method);
    }

    public static boolean isJunit4TestAnnotation(ASTAnnotation annot) {
        return TypeTestUtil.isA(JUNIT4_TEST_ANNOT, annot);
    }

    /**
     * Does not check the class (use {@link #isJUnit3Class(ASTAnyTypeDeclaration)}).
     */
    public static boolean isJunit3MethodSignature(ASTMethodDeclaration method) {
        return method.isVoid()
            && method.getVisibility() == Visibility.V_PUBLIC
            && method.getName().startsWith("test");
    }

    /**
     * True if this is a {@code TestCase} class for Junit 3.
     */
    public static boolean isJUnit3Class(ASTAnyTypeDeclaration node) {
        return node.isRegularClass()
            && !node.isNested()
            && !node.isAbstract()
            && TypeTestUtil.isA(JUNIT3_CLASS_NAME, node);
    }

    public static boolean isTestClass(ASTAnyTypeDeclaration node) {
        return node.isRegularClass() && !node.isAbstract() && !node.isNested()
            && (isJUnit3Class(node)
            || node.getDeclarations(ASTMethodDeclaration.class)
                   .any(TestFrameworksUtil::isTestMethod));
    }


    public static boolean isJUnit5NestedClass(ASTAnyTypeDeclaration innerClassDecl) {
        return innerClassDecl.isAnnotationPresent(JUNIT5_NESTED);
    }

    public static boolean isExpectExceptionCall(ASTMethodCall call) {
        return "expect".equals(call.getMethodName())
            && TypeTestUtil.isA("org.junit.rules.ExpectedException", call.getQualifier());
    }

    public static boolean isCallOnAssertionContainer(ASTMethodCall call) {
        JTypeMirror declaring = call.getMethodType().getDeclaringType();
        JTypeDeclSymbol sym = declaring.getSymbol();
        return sym instanceof JClassSymbol
                && (ASSERT_CONTAINERS.contains(((JClassSymbol) sym).getBinaryName())
                        || TypeTestUtil.isA("junit.framework.Assert", declaring));
    }

    public static boolean isProbableAssertCall(ASTMethodCall call) {
        String name = call.getMethodName();
        return name.startsWith("assert") && !isSoftAssert(call)
            || name.startsWith("check")
            || name.startsWith("verify")
            || "fail".equals(name)
            || "failWith".equals(name)
            || isExpectExceptionCall(call);
    }

    private static boolean isSoftAssert(ASTMethodCall call) {
        return TypeTestUtil.isA("org.assertj.core.api.AbstractSoftAssertions", call.getMethodType().getDeclaringType())
            && !"assertAll".equals(call.getMethodName());
    }

    /**
     * Tells if the node contains a @Test annotation with an expected exception.
     */
    public static boolean isExpectAnnotated(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations()
                     .filter(TestFrameworksUtil::isJunit4TestAnnotation)
                     .flatMap(ASTAnnotation::getMembers)
                     .any(it -> "expected".equals(it.getName()));

    }
}
