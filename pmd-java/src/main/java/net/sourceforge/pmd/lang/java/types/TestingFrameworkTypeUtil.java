/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 *  Public Utilities to check the type of testing framework
 */
public final class TestingFrameworkTypeUtil {

    private TestingFrameworkTypeUtil() {}

    public static final String JUNIT5_PACKAGE = "org.junit.jupiter.api.Test";
    public static final String JUNIT5_PARAMETERIZED_TEST_PACKAGE = "org.junit.jupiter.params.ParameterizedTest";

    /**
     *  Determine if the method is annotated with the @Test or @ParameterizedTest annotation from junit5
     * @param method the method node
     * @return true if the method is annotated with a junit5 annotation otherwise false
     */
    public static boolean isJunit5Test(ASTMethodDeclaration method) {
        return method.isAnnotationPresent(JUNIT5_PACKAGE)
                || method.isAnnotationPresent(JUNIT5_PARAMETERIZED_TEST_PACKAGE);
    }
}
