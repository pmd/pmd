package net.sourceforge.pmd.lang.java.ast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;

public class JDKVersionTest extends ParserTst {

    private static String loadSource(String name) {
        try {
            return IOUtils.toString(JDKVersionTest.class.getResourceAsStream("jdkversiontests/" + name), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        parseJava15(loadSource("jdk14_enum.java"));
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        parseJava14(loadSource("jdk14_enum.java"));
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        parseJava15(loadSource("jdk15_enum.java"));
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        parseJava15(loadSource("jdk14_enum.java"));
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        parseJava14(loadSource("assert_test1.java"));
        parseJava14(loadSource("assert_test2.java"));
        parseJava14(loadSource("assert_test3.java"));
        parseJava14(loadSource("assert_test4.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        parseJava14(loadSource("assert_test5.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        parseJava14(loadSource("assert_test7.java"));
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3() {
        parseJava13(loadSource("assert_test5.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith1_3() {
        parseJava13(loadSource("assert_test6.java"));
    }
    // assert keyword/identifier

    @Test
    public void testVarargsShouldPassWith15() throws Throwable {
        parseJava15(loadSource("jdk15_varargs.java"));
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() throws Throwable {
        parseJava14(loadSource("jdk15_varargs.java"));
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() throws Throwable {
        parseJava15(loadSource("jdk15_forloop.java"));
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() throws Throwable {
        parseJava15(loadSource("jdk15_forloop_with_modifier.java"));
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() throws Throwable {
        parseJava14(loadSource("jdk15_forloop.java"));
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() throws Throwable {
        parseJava15(loadSource("jdk15_generics.java"));
    }

    @Test
    public void testVariousParserBugs() throws Throwable {
        parseJava15(loadSource("fields_bug.java"));
        parseJava15(loadSource("gt_bug.java"));
        parseJava15(loadSource("annotations_bug.java"));
        parseJava15(loadSource("constant_field_in_annotation_bug.java"));
        parseJava15(loadSource("generic_in_field.java"));
    }

    @Test
    public void testNestedClassInMethodBug() throws Throwable {
        parseJava15(loadSource("inner_bug.java"));
        parseJava15(loadSource("inner_bug2.java"));
    }

    @Test
    public void testGenericsInMethodCall() throws Throwable {
        parseJava15(loadSource("generic_in_method_call.java"));
    }

    @Test
    public void testGenericINAnnotation() throws Throwable {
        parseJava15(loadSource("generic_in_annotation.java"));
    }

    @Test
    public void testGenericReturnType() throws Throwable {
        parseJava15(loadSource("generic_return_type.java"));
    }

    @Test
    public void testMultipleGenerics() throws Throwable {
        // See java/lang/concurrent/CopyOnWriteArraySet
        parseJava15(loadSource("funky_generics.java"));
        // See java/lang/concurrent/ConcurrentHashMap
        parseJava15(loadSource("multiple_generics.java"));
    }

    @Test
    public void testAnnotatedParams() throws Throwable {
        parseJava15(loadSource("annotated_params.java"));
    }

    @Test
    public void testAnnotatedLocals() throws Throwable {
        parseJava15(loadSource("annotated_locals.java"));
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3_test2() {
        parseJava13(loadSource("assert_test5_a.java"));
    }

    @Test
    public final void testBinaryAndUnderscoresInNumericalLiterals() throws Throwable {
        parseJava17(loadSource("jdk17_numerical_literals.java"));
    }

    @Test
    public final void testStringInSwitch() throws Throwable {
        parseJava17(loadSource("jdk17_string_in_switch.java"));
    }

    @Test
    public final void testGenericDiamond() throws Throwable {
        parseJava17(loadSource("jdk17_generic_diamond.java"));
    }

    @Test
    public final void testTryWithResources() throws Throwable {
        parseJava17(loadSource("jdk17_try_with_resources.java"));
    }

    @Test
    public final void testTryWithResourcesSemi() throws Throwable {
        parseJava17(loadSource("jdk17_try_with_resources_semi.java"));
    }

    @Test
    public final void testTryWithResourcesMulti() throws Throwable {
        parseJava17(loadSource("jdk17_try_with_resources_multi.java"));
    }

    @Test
    public final void testTryWithResourcesWithAnnotations() throws Throwable {
        parseJava17(loadSource("jdk17_try_with_resources_with_annotations.java"));
    }

    @Test
    public final void testMulticatch() throws Throwable {
        parseJava17(loadSource("jdk17_multicatch.java"));
    }

    @Test
    public final void testMulticatchWithAnnotations() throws Throwable {
        parseJava17(loadSource("jdk17_multicatch_with_annotations.java"));
    }
}
