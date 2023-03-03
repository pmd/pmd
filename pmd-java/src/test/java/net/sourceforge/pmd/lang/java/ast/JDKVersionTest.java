/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class JDKVersionTest extends BaseJavaTreeDumpTest {

    private final JavaParsingHelper java3 = JavaParsingHelper.DEFAULT
        .withDefaultVersion("1.3")
        .withResourceContext(JDKVersionTest.class, "jdkversiontests/");

    private final JavaParsingHelper java4 = java3.withDefaultVersion("1.4");
    private final JavaParsingHelper java5 = java3.withDefaultVersion("1.5");
    private final JavaParsingHelper java7 = java3.withDefaultVersion("1.7");
    private final JavaParsingHelper java8 = java3.withDefaultVersion("1.8");
    private final JavaParsingHelper java9 = java3.withDefaultVersion("9");

    // enum keyword/identifier
    @Test
    void testEnumAsKeywordShouldFailWith14() {
        assertThrows(ParseException.class, () -> java5.parseResource("jdk14_enum.java"));
    }

    @Test
    void testEnumAsIdentifierShouldPassWith14() {
        java4.parseResource("jdk14_enum.java");
    }

    @Test
    void testEnumAsKeywordShouldPassWith15() {
        java5.parseResource("jdk15_enum.java");
    }

    @Test
    void testEnumAsIdentifierShouldFailWith15() {
        assertThrows(ParseException.class, () -> java5.parseResource("jdk14_enum.java"));
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    void testAssertAsKeywordVariantsSucceedWith14() {
        java4.parseResource("assert_test1.java");
        java4.parseResource("assert_test2.java");
        java4.parseResource("assert_test3.java");
        java4.parseResource("assert_test4.java");
    }

    @Test
    void testAssertAsVariableDeclIdentifierFailsWith14() {
        assertThrows(ParseException.class, () -> java4.parseResource("assert_test5.java"));
    }

    @Test
    void testAssertAsMethodNameIdentifierFailsWith14() {
        assertThrows(ParseException.class, () -> java4.parseResource("assert_test7.java"));
    }

    @Test
    void testAssertAsIdentifierSucceedsWith13() {
        java3.parseResource("assert_test5.java");
    }

    @Test
    void testAssertAsKeywordFailsWith13() {
        assertThrows(ParseException.class, () -> java3.parseResource("assert_test6.java"));
    }
    // assert keyword/identifier

    @Test
    void testVarargsShouldPassWith15() {
        java5.parseResource("jdk15_varargs.java");
    }

    @Test
    void testGenericCtorCalls() {
        java5.parseResource("java5/generic_ctors.java");
    }

    @Test
    void testGenericSuperCtorCalls() {
        java5.parseResource("java5/generic_super_ctor.java");
    }

    @Test
    void testAnnotArrayInitializer() {
        java5.parseResource("java5/annotation_array_init.java");
    }

    @Test
    void testVarargsShouldFailWith14() {
        assertThrows(ParseException.class, () -> java4.parseResource("jdk15_varargs.java"));
    }

    @Test
    void testJDK15ForLoopSyntaxShouldPassWith15() {
        java5.parseResource("jdk15_forloop.java");
    }

    @Test
    void testJDK15ForLoopSyntaxWithModifiers() {
        java5.parseResource("jdk15_forloop_with_modifier.java");
    }

    @Test
    void testJDK15ForLoopShouldFailWith14() {
        assertThrows(ParseException.class, () -> java4.parseResource("jdk15_forloop.java"));
    }

    @Test
    void testJDK15GenericsSyntaxShouldPassWith15() {
        java5.parseResource("jdk15_generics.java");
    }

    @Test
    void testVariousParserBugs() {
        java5.parseResource("fields_bug.java");
        java5.parseResource("gt_bug.java");
        java5.parseResource("annotations_bug.java");
        java5.parseResource("constant_field_in_annotation_bug.java");
        java5.parseResource("generic_in_field.java");
    }

    @Test
    void testNestedClassInMethodBug() {
        java5.parseResource("inner_bug.java");
        java5.parseResource("inner_bug2.java");
    }

    @Test
    void testGenericsInMethodCall() {
        java5.parseResource("generic_in_method_call.java");
    }

    @Test
    void testGenericINAnnotation() {
        java5.parseResource("generic_in_annotation.java");
    }

    @Test
    void testGenericReturnType() {
        java5.parseResource("generic_return_type.java");
    }

    @Test
    void testMultipleGenerics() {
        // See java/lang/concurrent/CopyOnWriteArraySet
        java5.parseResource("funky_generics.java");
        // See java/lang/concurrent/ConcurrentHashMap
        java5.parseResource("multiple_generics.java");
    }

    @Test
    void testAnnotatedParams() {
        java5.parseResource("annotated_params.java");
    }

    @Test
    void testAnnotatedLocals() {
        java5.parseResource("annotated_locals.java");
    }

    @Test
    void testAssertAsIdentifierSucceedsWith13Test2() {
        java3.parseResource("assert_test5_a.java");
    }

    @Test
    void testBinaryAndUnderscoresInNumericalLiterals() {
        java7.parseResource("jdk17_numerical_literals.java");
    }

    @Test
    void testStringInSwitch() {
        java7.parseResource("jdk17_string_in_switch.java");
    }

    @Test
    void testGenericDiamond() {
        java7.parseResource("jdk17_generic_diamond.java");
    }

    @Test
    void testTryWithResources() {
        java7.parseResource("jdk17_try_with_resources.java");
    }

    @Test
    void testTryWithResourcesSemi() {
        java7.parseResource("jdk17_try_with_resources_semi.java");
    }

    @Test
    void testTryWithResourcesMulti() {
        java7.parseResource("jdk17_try_with_resources_multi.java");
    }

    @Test
    void testTryWithResourcesWithAnnotations() {
        java7.parseResource("jdk17_try_with_resources_with_annotations.java");
    }

    @Test
    void testMulticatch() {
        java7.parseResource("jdk17_multicatch.java");
    }

    @Test
    void testMulticatchWithAnnotations() {
        java7.parseResource("jdk17_multicatch_with_annotations.java");
    }

    @Test
    void jdk9PrivateInterfaceMethodsInJava18() {
        assertThrows(ParseException.class, () -> java8.parseResource("java9/jdk9_private_interface_methods.java"));
    }

    @Test
    void testPrivateMethods() {
        java8.parse("public class Foo { private void bar() { } }");
    }

    @Test
    void testTypeAnnotations() {
        java8.parseResource("java8/type_annotations.java");
    }

    @Test
    void testNestedPrivateMethods() {
        java8.parse("public interface Baz { public static class Foo { private void bar() { } } }");
    }

    @Test
    void jdk9PrivateInterfaceMethods() {
        java9.parseResource("java9/jdk9_private_interface_methods.java");
    }

    @Test
    void jdk9InvalidIdentifierInJava18() {
        java8.parseResource("java9/jdk9_invalid_identifier.java");
    }

    @Test
    void jdk9InvalidIdentifier() {
        assertThrows(ParseException.class, () -> java9.parseResource("java9/jdk9_invalid_identifier.java"));
    }

    @Test
    void jdk9AnonymousDiamondInJava8() {
        assertThrows(ParseException.class, () -> java8.parseResource("java9/jdk9_anonymous_diamond.java"));
    }

    @Test
    void jdk9AnonymousDiamond() {
        java9.parseResource("java9/jdk9_anonymous_diamond.java");
    }

    @Test
    void jdk9ModuleInfoInJava8() {
        assertThrows(ParseException.class, () -> java8.parseResource("java9/jdk9_module_info.java"));
    }

    @Test
    void jdk9ModuleInfo() {
        java9.parseResource("java9/jdk9_module_info.java");
    }

    @Test
    void testAnnotatedModule() {
        java9.parseResource("java9/jdk9_module_info_with_annot.java");
    }

    @Test
    void jdk9TryWithResourcesInJava8() {
        assertThrows(ParseException.class, () -> java8.parseResource("java9/jdk9_try_with_resources.java"));
    }

    @Test
    void jdk9TryWithResources() {
        java9.parseResource("java9/jdk9_try_with_resources.java");
    }

    @Test
    void jdk7PrivateMethodInnerClassInterface1() {
        ASTCompilationUnit acu = java7.parseResource("private_method_in_inner_class_interface1.java");
        List<ASTMethodDeclaration> methods = acu.findDescendantsOfType(ASTMethodDeclaration.class, true);
        assertEquals(3, methods.size());
        for (ASTMethodDeclaration method : methods) {
            assertFalse(method.getEnclosingType().isInterface());
        }
    }

    @Test
    void jdk7PrivateMethodInnerClassInterface2() {
        ParseException thrown = assertThrows(ParseException.class, () -> java7.parseResource("private_method_in_inner_class_interface2.java"));
        assertTrue(thrown.getMessage().contains("line 19"));
    }

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return java9;
    }
}
