/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.BaseParserTest;

public class TestingFrameworkTypeUtilTest extends BaseParserTest {

    @Test
    public void methodAnnotatedWithJunit5TestAnnotationTest() {
        ASTMethodDeclaration methodDeclaration = java.parse("import org.junit.jupiter.api.Test; "
                + "class SomeTest { @Test void test() {}}").getFirstDescendantOfType(ASTMethodDeclaration.class);

        Assert.assertTrue("Because the method is annotated with the junit5 @Test the result should be true ",
                TestingFrameworkTypeUtil.isJunit5Test(methodDeclaration));

    }

    @Test
    public void methodAnnotatedWithJunit5ParameterizedTestAnnotationTest() {
        ASTMethodDeclaration methodDeclaration = java.parse("import org.junit.jupiter.params.ParameterizedTest; "
                + "class SomeTest { @ParameterizedTest void test() {}}").getFirstDescendantOfType(ASTMethodDeclaration.class);

        Assert.assertTrue("Because the method is annotated with the junit5 #ParameterizedTest the result should be true ",
                TestingFrameworkTypeUtil.isJunit5Test(methodDeclaration));

    }

    @Test
    public void methodNotAnnotatedWithJunit5Annotation() {
        ASTMethodDeclaration methodDeclaration = java.parse(
                "class SomeTest {  void test() {}}").getFirstDescendantOfType(ASTMethodDeclaration.class);

        Assert.assertFalse("Because the method is not annotated with junit 5 annotations it should not be true ",
                TestingFrameworkTypeUtil.isJunit5Test(methodDeclaration));

    }

}
