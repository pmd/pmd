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
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Utilities for junit-related rules.
 */
public final class JUnitRuleUtil {

    private static final String JUNIT3_CLASS_NAME = "junit.framework.TestCase";
    private static final String JUNIT4_TEST_ANNOT = "org.junit.Test";
    private static final String JUNIT5_TEST_ANNOT = "org.junit.jupiter.api.Test";

    private static final String TESTNG_TEST_ANNOT = "org.testng.annotations.Test";

    private static final Set<String> JUNIT5_ALL_TEST_ANNOTS =
        setOf("org.junit.jupiter.api.Test",
              "org.junit.jupiter.api.RepeatedTest",
              "org.junit.jupiter.api.TestFactory",
              "org.junit.jupiter.api.TestTemplate",
              "org.junit.jupiter.params.ParameterizedTest"
        );

    private static final Set<String> ASSERT_CONTAINERS = setOf("org.junit.Assert",
                                                               "org.junit.jupiter.api.Assertions",
                                                               "org.hamcrest.MatcherAssert",
                                                               "org.testng.Assert",
                                                               "junit.framework.TestCase");

    private JUnitRuleUtil() {
        // utility class
    }

    /**
     * True if this is a junit @Test method (or a junit 3 method).
     */
    public static boolean isJUnitMethod(ASTMethodDeclaration method) {
        if (method.isStatic() || method.getBody() == null) {
            return false; // skip various inapplicable method variations
        }

        boolean result = false;
        result = result || isJUnit5Method(method);
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

    private static boolean isTestNgMethod(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(TESTNG_TEST_ANNOT);
    }

    private static boolean isJUnit4Method(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(JUNIT4_TEST_ANNOT) && method.isPublic();
    }

    private static boolean isJUnit5Method(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations().any(
            it -> {
                String canonicalName = it.getTypeMirror().getSymbol().getCanonicalName();
                return JUNIT5_ALL_TEST_ANNOTS.contains(canonicalName);
            }
        );
    }

    private static boolean isJUnit3Method(ASTMethodDeclaration method) {
        return TypeTestUtil.isA("junit.framework.TestCase", method.getEnclosingType())
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
            && method.isPublic()
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

    public static boolean isExpectExceptionCall(ASTMethodCall call) {
        return "expect".equals(call.getMethodName()) && TypeTestUtil.isA("org.junit.rules.ExpectedException", call.getQualifier());
    }

    public static boolean isCallOnAssertionContainer(ASTMethodCall call) {
        return isCallOnType(call, ASSERT_CONTAINERS);
    }

    private static boolean isCallOnType(ASTMethodCall call, Set<String> qualifierTypes) {
        JTypeMirror declaring = call.getMethodType().getDeclaringType();
        JTypeDeclSymbol sym = declaring.getSymbol();
        String binaryName = !(sym instanceof JClassSymbol) ? null : ((JClassSymbol) sym).getBinaryName();
        return qualifierTypes.contains(binaryName);
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
                     .filter(JUnitRuleUtil::isJunit4TestAnnotation)
                     .flatMap(ASTAnnotation::getMembers)
                     .any(it -> "expected".equals(it.getName()));

    }
}
