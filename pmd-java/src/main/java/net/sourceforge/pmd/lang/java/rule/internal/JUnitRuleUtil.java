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
    private static final Set<String> MOCKITO = setOf("org.mockito.Mockito");
    private static final Set<String> ASSERT_CONTAINERS = setOf("org.junit.Assert",
                                                               "org.junit.jupiter.api.Assertions",
                                                               "org.hamcrest.MatcherAssert",
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

    private static boolean isJUnit4Method(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(JUNIT4_TEST_ANNOT) && method.isPublic();
    }

    private static boolean isJUnit5Method(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(JUNIT5_TEST_ANNOT);
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

    /**
     * True if this is a call to an assert/fail method. Supports a lot
     * of different patterns.
     */
    public static boolean isAssertCall(ASTMethodCall call) {
        String name = call.getMethodName();
        return "expect".equals(name) && TypeTestUtil.isA("org.junit.rules.ExpectedException", call.getQualifier())
            || "assertAll".equals(name) && TypeTestUtil.isA("org.assertj.core.api.SoftAssertions", call.getQualifier())
            || "verify".equals(name) && isCallOnType(call, MOCKITO)
            || (name.startsWith("assert") || "fail".equals(name)) && isCallOnAssertionContainer(call);
    }

    private static boolean isCallOnAssertionContainer(ASTMethodCall call) {
        return isCallOnType(call, ASSERT_CONTAINERS);
    }

    private static boolean isCallOnType(ASTMethodCall call, Set<String> qualifierTypes) {
        JTypeMirror declaring = call.getMethodType().getDeclaringType();
        JTypeDeclSymbol sym = declaring.getSymbol();
        String binaryName = !(sym instanceof JClassSymbol) ? null : ((JClassSymbol) sym).getBinaryName();
        return qualifierTypes.contains(binaryName);
    }
}
