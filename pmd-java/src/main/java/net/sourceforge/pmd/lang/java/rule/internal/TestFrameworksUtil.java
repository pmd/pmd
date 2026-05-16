/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Utilities for rules related to test frameworks (Junit, TestNG, etc).
 */
public final class TestFrameworksUtil {

    private static final String JUNIT3_CLASS_NAME = "junit.framework.TestCase";
    private static final String JUNIT4_TEST_ANNOT = "org.junit.Test";

    private static final String TEST_NG_TEST_ANNOT = "org.testng.annotations.Test";

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

    private static final Set<String> TEST_CONFIGURATION_ANNOTATIONS =
        setOf("org.junit.Before",
                "org.junit.BeforeClass",
                "org.junit.After",
                "org.junit.AfterClass",
                "org.junit.jupiter.api.BeforeEach",
                "org.junit.jupiter.api.BeforeAll",
                "org.junit.jupiter.api.AfterEach",
                "org.junit.jupiter.api.AfterAll",
                "org.testng.annotations.AfterClass",
                "org.testng.annotations.AfterGroups",
                "org.testng.annotations.AfterMethod",
                "org.testng.annotations.AfterSuite",
                "org.testng.annotations.AfterTest",
                "org.testng.annotations.BeforeClass",
                "org.testng.annotations.BeforeGroups",
                "org.testng.annotations.BeforeMethod",
                "org.testng.annotations.BeforeSuite",
                "org.testng.annotations.BeforeTest"
        );

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
        return TEST_CONFIGURATION_ANNOTATIONS.stream().anyMatch(method::isAnnotationPresent)
                || isJUnit3Class(method.getEnclosingType())
                        && ("setUp".equals(method.getName())
                            || "tearDown".equals(method.getName()));
    }

    private static boolean isTestNgMethod(ASTMethodDeclaration method) {
        return isTestNgMethod(method.getSymbol());
    }

    private static boolean isTestNgMethod(JMethodSymbol methodSymbol) {
        return methodSymbol.getDeclaredAnnotations().stream().anyMatch(
                it -> TEST_NG_TEST_ANNOT.equals(it.getBinaryName())
        );
    }

    public static boolean isJUnit4Method(ASTMethodDeclaration method) {
        return isJUnit4Method(method.getSymbol());
    }

    private static boolean isJUnit4Method(JMethodSymbol methodSymbol) {
        return methodSymbol.getDeclaredAnnotations().stream().anyMatch(
                it -> JUNIT4_TEST_ANNOT.equals(it.getBinaryName())
        );
    }

    public static boolean isJUnit5Method(ASTMethodDeclaration method) {
        return isJUnit5Method(method.getSymbol());
    }

    private static boolean isJUnit5Method(JMethodSymbol methodSymbol) {
        return methodSymbol.getDeclaredAnnotations().stream().anyMatch(
                it -> JUNIT5_ALL_TEST_ANNOTS.contains(it.getBinaryName())
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
     * Does not check the class (use {@link #isJUnit3Class(ASTTypeDeclaration)}).
     */
    public static boolean isJunit3MethodSignature(ASTMethodDeclaration method) {
        return method.isVoid()
            && method.getVisibility() == Visibility.V_PUBLIC
            && method.getName().startsWith("test");
    }

    /**
     * True if this is a {@code TestCase} class for Junit 3.
     */
    public static boolean isJUnit3Class(ASTTypeDeclaration node) {
        return node != null
            && node.isRegularClass()
            && !node.isNested()
            && !node.isAbstract()
            && TypeTestUtil.isA(JUNIT3_CLASS_NAME, node);
    }

    public static boolean isTestClass(ASTClassDeclaration node) {
        return isJUnit3Class(node) || isJUnit4Class(node) || isJUnit5Class(node) || isTestNGClass(node);
    }

    public static boolean isJUnit4Class(ASTClassDeclaration node) {
        JClassType typeMirror = node.getTypeMirror();

        return !typeMirror.isInterface() && !typeMirror.getSymbol().isAbstract() && typeMirror.getEnclosingType() == null
                && typeMirror.streamMethods(TestFrameworksUtil::isJUnit4Method)
                        .findAny().isPresent();
    }

    public static boolean isJUnit5Class(ASTClassDeclaration node) {
        JClassType typeMirror = node.getTypeMirror();

        return !typeMirror.isInterface() && !typeMirror.getSymbol().isAbstract() && typeMirror.getEnclosingType() == null
                && typeMirror.streamMethods(TestFrameworksUtil::isJUnit5Method)
                        .findAny().isPresent();
    }

    public static boolean isTestNGClass(ASTClassDeclaration node) {
        JClassType typeMirror = node.getTypeMirror();

        return !typeMirror.isInterface() && !typeMirror.getSymbol().isAbstract() && typeMirror.getEnclosingType() == null
                && typeMirror.streamMethods(TestFrameworksUtil::isTestNgMethod)
                        .findAny().isPresent();
    }

    public static boolean isJUnit5NestedClass(ASTTypeDeclaration innerClassDecl) {
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
        boolean isSoftAssertType = isSoftAssert(call);
        return name.startsWith("assert") && !isSoftAssertType
            || "assertAll".equals(name) && isSoftAssertType
            || "assertSoftly".equals(name) && isSoftAssertType
            || name.startsWith("check")
            || name.startsWith("verify")
            || "fail".equals(name)
            || "failWith".equals(name)
            || isExpectExceptionCall(call);
    }

    public static boolean isSoftAssert(ASTMethodCall call) {
        JTypeMirror declaringType = call.getMethodType().getDeclaringType();
        return TypeTestUtil.isA("org.assertj.core.api.StandardSoftAssertionsProvider", declaringType)
                || TypeTestUtil.isA("org.assertj.core.api.Java6StandardSoftAssertionsProvider", declaringType)
                || TypeTestUtil.isA("org.assertj.core.api.AbstractSoftAssertions", declaringType);
    }

    /**
     * Tells if the node contains a @Test annotation with an expected exception.
     */
    public static boolean isExpectAnnotated(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations()
                     .filter(annotation -> isJunit4TestAnnotation(annotation) || isTestNgMethod(method))
                     .flatMap(ASTAnnotation::getMembers)
                     .any(it -> "expected".equals(it.getName()) || "expectedExceptions".equals(it.getName()));

    }
}
