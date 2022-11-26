/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static net.sourceforge.pmd.RuleViolation.CLASS_NAME;
import static net.sourceforge.pmd.RuleViolation.METHOD_NAME;
import static net.sourceforge.pmd.RuleViolation.PACKAGE_NAME;
import static net.sourceforge.pmd.RuleViolation.VARIABLE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.mutable.MutableObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleContext;
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
class JavaRuleViolationTest {

    // TODO there are no tests for anon or local classes

    @Test
    void testASTFormalParameterVariableName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        final ASTFormalParameter node = ast.descendants(ASTFormalParameter.class).first();
        final RuleViolation violation = violationAt(node);
        assertThat(violation.getAdditionalInfo(), hasEntry(VARIABLE_NAME, "x"));
        assertThat(violation.getAdditionalInfo(), hasEntry(METHOD_NAME, "bar"));
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
        assertThat(violationAt(md).getAdditionalInfo(), hasEntry(METHOD_NAME, "bar"));
    }

    public @NonNull RuleViolation violationAt(JavaNode md) {
        MutableObject<RuleViolation> rv = new MutableObject<>();
        RuleContext rctx = RuleContext.create(rv::setValue, new FooRule());
        rctx.addViolation(md);
        return Objects.requireNonNull(rv.getValue());
    }

    /**
     * Tests that the enum name is taken correctly from the given node.
     */
    @Test
    void testEnumName() {
        ASTCompilationUnit ast = parse("enum Foo {FOO; void bar(int x) {} }");
        ASTMethodDeclaration md = ast.descendants(ASTMethodDeclaration.class).first();
        assertThat(violationAt(md).getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));
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

        RuleViolation violation = violationAt(importNode);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndClassNameForField() {
        ASTCompilationUnit ast = parse("package pkg; public class Foo { int a; }");
        ASTClassOrInterfaceDeclaration classDeclaration = ast.descendants(ASTClassOrInterfaceDeclaration.class).first();
        ASTFieldDeclaration field = ast.descendants(ASTFieldDeclaration.class).first();

        RuleViolation violation = violationAt(classDeclaration);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));

        violation = violationAt(field);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndEnumName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public enum FooE { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        RuleViolation violation = violationAt(importNode);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "FooE"));
    }

    @Test
    void testDefaultPackageAndClassName() {
        ASTCompilationUnit ast = parse("import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        RuleViolation violation = violationAt(importNode);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, ""));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));
    }

    @Test
    void testPackageAndMultipleClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { } public class Bar { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        RuleViolation violation = violationAt(importNode);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Bar"));
    }

    @Test
    void testPackageAndPackagePrivateClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { }");
        ASTImportDeclaration importNode = ast.descendants(ASTImportDeclaration.class).first();

        RuleViolation violation = violationAt(importNode);
        assertThat(violation.getAdditionalInfo(), hasEntry(PACKAGE_NAME, "pkg"));
        assertThat(violation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));
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

        RuleViolation fooViolation = violationAt(classes.get(0));
        assertThat(fooViolation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));

        RuleViolation barViolation = violationAt(classes.get(1));
        assertThat(barViolation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Bar"));

        List<ASTFieldDeclaration> fields = ast.descendants(ASTFieldDeclaration.class).crossFindBoundaries().toList();
        assertEquals(2, fields.size());

        RuleViolation fieldViolation = violationAt(fields.get(0));
        assertThat(fieldViolation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Foo"));

        RuleViolation innerFieldViolation = violationAt(fields.get(1));
        assertThat(innerFieldViolation.getAdditionalInfo(), hasEntry(CLASS_NAME, "Bar"));
    }

}
