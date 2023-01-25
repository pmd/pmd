/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.RuleViolation.CLASS_NAME;
import static net.sourceforge.pmd.RuleViolation.METHOD_NAME;
import static net.sourceforge.pmd.RuleViolation.PACKAGE_NAME;
import static net.sourceforge.pmd.RuleViolation.VARIABLE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * @author Philip Graf
 */
class JavaViolationDecoratorTest {

    // TODO there are no tests for anon or local classes

    @Test
    void testASTFormalParameterVariableName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        ASTFormalParameter node = ast.descendants(ASTFormalParameter.class).first();
        Map<String, String> info = decorate(node);
        assertThat(info, hasEntry(VARIABLE_NAME, "x"));
        assertThat(info, hasEntry(METHOD_NAME, "bar"));
    }

    private ASTCompilationUnit parse(final String code) {
        return JavaParsingHelper.DEFAULT.parse(code);
    }

    /**
     * Tests that the method name is taken correctly from the given node.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1250/">#1250</a>
     */
    @Test
    void testMethodName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        ASTMethodDeclaration md = ast.descendants(ASTMethodDeclaration.class).first();
        assertThat(decorate(md), hasEntry(METHOD_NAME, "bar"));
    }

    static Map<String, String> decorate(JavaNode md) {
        Map<String, String> result = new HashMap<>();
        JavaViolationDecorator.INSTANCE.decorate(md, result);
        return result;
    }

    /**
     * Tests that the enum name is taken correctly from the given node.
     */
    @Test
    void testEnumName() {
        ASTCompilationUnit ast = parse("enum Foo {FOO; void bar(int x) {} }");
        ASTMethodDeclaration md = ast.descendants(ASTMethodDeclaration.class).first();
        assertThat(decorate(md), hasEntry(CLASS_NAME, "Foo"));
    }

    /**
     * Tests that the class name is taken correctly, even if the node is outside
     * of a class scope, e.g. a import declaration.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1529/">#1529</a>
     */
    @Test
    void testPackageAndClassNameForImport() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        Map<String, String> violation = decorate(importNode);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndClassNameForField() {
        ASTCompilationUnit ast = parse("package pkg; public class Foo { int a; }");
        ASTClassOrInterfaceDeclaration classDeclaration = ast.descendants(ASTClassOrInterfaceDeclaration.class).first();
        ASTFieldDeclaration field = ast.descendants(ASTFieldDeclaration.class).first();

        Map<String, String> violation = decorate(classDeclaration);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "Foo"));

        violation = decorate(field);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndEnumName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public enum FooE { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        Map<String, String> violation = decorate(importNode);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "FooE"));
    }

    @Test
    void testDefaultPackageAndClassName() {
        ASTCompilationUnit ast = parse("import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        Map<String, String> violation = decorate(importNode);
        assertThat(violation, hasEntry(PACKAGE_NAME, ""));
        assertThat(violation, hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndMultipleClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { } public class Bar { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        Map<String, String> violation = decorate(importNode);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "Bar"));
    }

    @Test
    void testPackageAndPackagePrivateClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        Map<String, String> violation = decorate(importNode);
        assertThat(violation, hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation, hasEntry(CLASS_NAME, "Foo"));
    }

    /**
     * Test that the name of the inner class is taken correctly.
     * Also check fields.
     */
    @Test
    void testInnerClass() {
        ASTCompilationUnit ast = parse("class Foo { int a; class Bar { int a; } }");
        List<ASTClassOrInterfaceDeclaration> classes = ast.descendants(ASTClassOrInterfaceDeclaration.class).toList();
        assertEquals(2, classes.size());

        assertThat(decorate(classes.get(0)), hasEntry(CLASS_NAME, "Foo"));
        assertThat(decorate(classes.get(1)), hasEntry(CLASS_NAME, "Bar"));

        List<ASTFieldDeclaration> fields = ast.descendants(ASTFieldDeclaration.class).crossFindBoundaries().toList();
        assertEquals(2, fields.size());

        assertThat(decorate(fields.get(0)), hasEntry(CLASS_NAME, "Foo"));
        assertThat(decorate(fields.get(1)), hasEntry(CLASS_NAME, "Bar"));
    }

    @Test
    void testInitializers() {
        ASTCompilationUnit ast = parse("class Foo { int a = 1;  { int x = 2; } }");
        List<ASTNumericLiteral> expressions = ast.descendants(ASTNumericLiteral.class).toList();
        assertEquals(2, expressions.size());

        assertThat(decorate(expressions.get(0)), hasEntry(CLASS_NAME, "Foo"));
        assertThat(decorate(expressions.get(0)), hasEntry(VARIABLE_NAME, "a"));

        assertThat(decorate(expressions.get(1)), hasEntry(CLASS_NAME, "Foo"));
        assertThat(decorate(expressions.get(1)), hasEntry(VARIABLE_NAME, "x"));
    }

}
