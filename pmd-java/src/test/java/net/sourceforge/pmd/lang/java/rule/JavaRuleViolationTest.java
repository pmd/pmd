/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * @author Philip Graf
 */
public class JavaRuleViolationTest {

    // TODO there are no tests for anon or local classes

    /**
     * Verifies that {@link JavaRuleViolation} sets the variable name for an
     * {@link ASTFormalParameter} node.
     */
    @Test
    public void testASTFormalParameterVariableName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        final ASTFormalParameter node = ast.getFirstDescendantOfType(ASTFormalParameter.class);
        final RuleViolation violation = violationAt(node);
        assertEquals("x", violation.getVariableName());
    }

    private ASTCompilationUnit parse(final String code) {
        return JavaParsingHelper.WITH_PROCESSING.parse(code);
    }

    /**
     * Tests that the method name is taken correctly from the given node.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1250/">#1250</a>
     */
    @Test
    public void testMethodName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        ASTMethodDeclaration md = ast.getFirstDescendantOfType(ASTMethodDeclaration.class);
        assertEquals("bar", violationAt(md).getMethodName());
    }

    @NonNull
    public RuleViolation violationAt(JavaNode md) {
        return new JavaRuleViolation(new FooRule(), md, "", "");
    }

    /**
     * Tests that the enum name is taken correctly from the given node.
     */
    @Test
    public void testEnumName() {
        ASTCompilationUnit ast = parse("enum Foo {FOO; void bar(int x) {} }");
        ASTMethodDeclaration md = ast.getFirstDescendantOfType(ASTMethodDeclaration.class);
        assertEquals("Foo", violationAt(md).getClassName());
    }

    /**
     * Tests that the class name is taken correctly, even if the node is outside
     * of a class scope, e.g. a import declaration.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1529/">#1529</a>
     */
    @Test
    public void testPackageAndClassNameForImport() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);

        RuleViolation violation = violationAt(importNode);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("Foo", violation.getClassName());
    }

    @Test
    public void testPackageAndClassNameForField() {
        ASTCompilationUnit ast = parse("package pkg; public class Foo { int a; }");
        ASTClassOrInterfaceDeclaration classDeclaration = ast.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        ASTFieldDeclaration field = ast.getFirstDescendantOfType(ASTFieldDeclaration.class);

        RuleViolation violation = violationAt(classDeclaration);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("Foo", violation.getClassName());

        violation = violationAt(field);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("Foo", violation.getClassName());
    }

    @Test
    public void testPackageAndEnumName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public enum FooE { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);

        RuleViolation violation = violationAt(importNode);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("FooE", violation.getClassName());
    }

    @Test
    public void testDefaultPackageAndClassName() {
        ASTCompilationUnit ast = parse("import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);

        RuleViolation violation = violationAt(importNode);
        assertEquals("", violation.getPackageName());
        assertEquals("Foo", violation.getClassName());
    }

    @Test
    public void testPackageAndMultipleClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { } public class Bar { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);

        RuleViolation violation = violationAt(importNode);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("Bar", violation.getClassName());
    }

    @Test
    public void testPackageAndPackagePrivateClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);

        RuleViolation violation = violationAt(importNode);
        assertEquals("pkg", violation.getPackageName());
        assertEquals("Foo", violation.getClassName());
    }

    /**
     * Test that the name of the inner class is taken correctly.
     * Also check fields.
     */
    @Test
    public void testInnerClass() {
        ASTCompilationUnit ast = parse("class Foo { int a; class Bar { int a; } }");
        List<ASTClassOrInterfaceDeclaration> classes = ast.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class);
        assertEquals(2, classes.size());

        RuleViolation fooViolation = violationAt(classes.get(0));
        assertEquals("Foo", fooViolation.getClassName());

        RuleViolation barViolation = violationAt(classes.get(1));
        assertEquals("Foo$Bar", barViolation.getClassName());

        List<ASTFieldDeclaration> fields = ast.findDescendantsOfType(ASTFieldDeclaration.class, true);
        assertEquals(2, fields.size());

        RuleViolation fieldViolation = violationAt(fields.get(0));
        assertEquals("Foo", fieldViolation.getClassName());

        RuleViolation innerFieldViolation = violationAt(fields.get(1));
        assertEquals("Foo$Bar", innerFieldViolation.getClassName());
    }

}
