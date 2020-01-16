/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class JDKVersionTest {

    private final JavaParsingHelper java3 = JavaParsingHelper.JUST_PARSE
        .withDefaultVersion("1.3")
        .withResourceContext(JDKVersionTest.class, "jdkversiontests/");

    private final JavaParsingHelper java4 = java3.withDefaultVersion("1.4");
    private final JavaParsingHelper java5 = java3.withDefaultVersion("1.5");
    private final JavaParsingHelper java7 = java3.withDefaultVersion("1.7");
    private final JavaParsingHelper java8 = java3.withDefaultVersion("1.8");
    private final JavaParsingHelper java9 = java3.withDefaultVersion("9");

    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() {
        java5.parseResource("jdk14_enum.java");
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() {
        java4.parseResource("jdk14_enum.java");
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() {
        java5.parseResource("jdk15_enum.java");
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() {
        java5.parseResource("jdk14_enum.java");
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith14() {
        java4.parseResource("assert_test1.java");
        java4.parseResource("assert_test2.java");
        java4.parseResource("assert_test3.java");
        java4.parseResource("assert_test4.java");
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith14() {
        java4.parseResource("assert_test5.java");
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith14() {
        java4.parseResource("assert_test7.java");
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith13() {
        java3.parseResource("assert_test5.java");
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith13() {
        java3.parseResource("assert_test6.java");
    }
    // assert keyword/identifier

    @Test
    public void testVarargsShouldPassWith15() {
        java5.parseResource("jdk15_varargs.java");
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() {
        java4.parseResource("jdk15_varargs.java");
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() {
        java5.parseResource("jdk15_forloop.java");
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() {
        java5.parseResource("jdk15_forloop_with_modifier.java");
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() {
        java4.parseResource("jdk15_forloop.java");
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() {
        java5.parseResource("jdk15_generics.java");
    }

    @Test
    public void testVariousParserBugs() {
        java5.parseResource("fields_bug.java");
        java5.parseResource("gt_bug.java");
        java5.parseResource("annotations_bug.java");
        java5.parseResource("constant_field_in_annotation_bug.java");
        java5.parseResource("generic_in_field.java");
    }

    @Test
    public void testNestedClassInMethodBug() {
        java5.parseResource("inner_bug.java");
        java5.parseResource("inner_bug2.java");
    }

    @Test
    public void testGenericsInMethodCall() {
        java5.parseResource("generic_in_method_call.java");
    }

    @Test
    public void testGenericINAnnotation() {
        java5.parseResource("generic_in_annotation.java");
    }

    @Test
    public void testGenericReturnType() {
        java5.parseResource("generic_return_type.java");
    }

    @Test
    public void testMultipleGenerics() {
        // See java/lang/concurrent/CopyOnWriteArraySet
        java5.parseResource("funky_generics.java");
        // See java/lang/concurrent/ConcurrentHashMap
        java5.parseResource("multiple_generics.java");
    }

    @Test
    public void testAnnotatedParams() {
        java5.parseResource("annotated_params.java");
    }

    @Test
    public void testAnnotatedLocals() {
        java5.parseResource("annotated_locals.java");
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith13Test2() {
        java3.parseResource("assert_test5_a.java");
    }

    @Test
    public final void testBinaryAndUnderscoresInNumericalLiterals() {
        java7.parseResource("jdk17_numerical_literals.java");
    }

    @Test
    public final void testStringInSwitch() {
        java7.parseResource("jdk17_string_in_switch.java");
    }

    @Test
    public final void testGenericDiamond() {
        java7.parseResource("jdk17_generic_diamond.java");
    }

    @Test
    public final void testTryWithResources() {
        java7.parseResource("jdk17_try_with_resources.java");
    }

    @Test
    public final void testTryWithResourcesSemi() {
        java7.parseResource("jdk17_try_with_resources_semi.java");
    }

    @Test
    public final void testTryWithResourcesMulti() {
        java7.parseResource("jdk17_try_with_resources_multi.java");
    }

    @Test
    public final void testTryWithResourcesWithAnnotations() {
        java7.parseResource("jdk17_try_with_resources_with_annotations.java");
    }

    @Test
    public final void testMulticatch() {
        java7.parseResource("jdk17_multicatch.java");
    }

    @Test
    public final void testMulticatchWithAnnotations() {
        java7.parseResource("jdk17_multicatch_with_annotations.java");
    }

    @Test(expected = ParseException.class)
    public final void jdk9PrivateInterfaceMethodsInJava18() {
        java8.parseResource("jdk9_private_interface_methods.java");
    }

    @Test
    public final void testPrivateMethods() {
        java8.parse("public class Foo { private void bar() { } }");
    }

    @Test
    public final void testNestedPrivateMethods() {
        java8.parse("public interface Baz { public static class Foo { private void bar() { } } }");
    }

    @Test
    public final void jdk9PrivateInterfaceMethods() {
        java9.parseResource("jdk9_private_interface_methods.java");
    }

    @Test
    public final void jdk9InvalidIdentifierInJava18() {
        java8.parseResource("jdk9_invalid_identifier.java");
    }

    @Test(expected = ParseException.class)
    public final void jdk9InvalidIdentifier() {
        java9.parseResource("jdk9_invalid_identifier.java");
    }

    @Test(expected = ParseException.class)
    public final void jdk9AnonymousDiamondInJava8() {
        java8.parseResource("jdk9_anonymous_diamond.java");
    }

    @Test
    public final void jdk9AnonymousDiamond() {
        java9.parseResource("jdk9_anonymous_diamond.java");
    }

    @Test(expected = ParseException.class)
    public final void jdk9ModuleInfoInJava8() {
        java8.parseResource("jdk9_module_info.java");
    }

    @Test
    public final void jdk9ModuleInfo() {
        java9.parseResource("jdk9_module_info.java");
    }

    @Test
    public void testAnnotatedModule() {
        java9.parseResource("jdk9_module_info_with_annot.java");
    }

    @Test(expected = ParseException.class)
    public final void jdk9TryWithResourcesInJava8() {
        java8.parseResource("jdk9_try_with_resources.java");
    }

    @Test
    public final void jdk9TryWithResources() {
        java9.parseResource("jdk9_try_with_resources.java");
    }

    @Test
    public final void jdk7PrivateMethodInnerClassInterface1() {
        ASTCompilationUnit acu = java7.parseResource("private_method_in_inner_class_interface1.java");
        List<ASTMethodDeclaration> methods = acu.findDescendantsOfType(ASTMethodDeclaration.class, true);
        assertEquals(3, methods.size());
        for (ASTMethodDeclaration method : methods) {
            assertFalse(method.isInterfaceMember());
        }
    }

    @Test
    public final void jdk7PrivateMethodInnerClassInterface2() {
        try {
            ASTCompilationUnit acu = java7.parseResource("private_method_in_inner_class_interface2.java");
            fail("Expected exception");
        } catch (ParseException e) {
            assertTrue(e.getMessage().startsWith("Line 19"));
        }
    }
}
