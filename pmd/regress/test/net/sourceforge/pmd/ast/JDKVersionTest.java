package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ParseException;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

public class JDKVersionTest extends ParserTst {

    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        parseJava15(JDK14_ENUM);
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        parseJava14(JDK14_ENUM);
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        parseJava15(JDK15_ENUM);
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        parseJava15(JDK14_ENUM);
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        parseJava14(ASSERT_TEST1);
        parseJava14(ASSERT_TEST2);
        parseJava14(ASSERT_TEST3);
        parseJava14(ASSERT_TEST4);
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        parseJava14(ASSERT_TEST5);
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        parseJava14(ASSERT_TEST7);
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3() {
        parseJava13(ASSERT_TEST5);
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith1_3() {
        parseJava13(ASSERT_TEST6);
    }
    // assert keyword/identifier

    @Test
    public void testVarargsShouldPassWith15() throws Throwable {
        parseJava15(JDK15_VARARGS);
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() throws Throwable {
        parseJava14(JDK15_VARARGS);
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() throws Throwable {
        parseJava15(JDK15_FORLOOP);
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() throws Throwable {
        parseJava15(JDK15_FORLOOP_WITH_MODIFIER);
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() throws Throwable {
        parseJava14(JDK15_FORLOOP);
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() throws Throwable {
        parseJava15(JDK15_GENERICS);
    }

    @Test
    public void testVariousParserBugs() throws Throwable {
        parseJava15(FIELDS_BUG);
        parseJava15(GT_BUG);
        parseJava15(ANNOTATIONS_BUG);
        parseJava15(GENERIC_IN_FIELD);
    }

    @Test
    public void testNestedClassInMethodBug() throws Throwable {
        parseJava15(INNER_BUG);
        parseJava15(INNER_BUG2);
    }

    @Test
    public void testGenericsInMethodCall() throws Throwable {
        parseJava15(GENERIC_IN_METHOD_CALL);
    }

    @Test
    public void testGenericINAnnotation() throws Throwable {
        parseJava15(GENERIC_IN_ANNOTATION);
    }

    @Test
    public void testGenericReturnType() throws Throwable {
        parseJava15(GENERIC_RETURN_TYPE);
    }

    @Test
    public void testMultipleGenerics() throws Throwable {
        parseJava15(FUNKY_GENERICS);
        parseJava15(MULTIPLE_GENERICS);
    }

    @Test
    public void testAnnotatedParams() throws Throwable {
        parseJava15(ANNOTATED_PARAMS);
    }

    @Test
    public void testAnnotatedLocals() throws Throwable {
        parseJava15(ANNOTATED_LOCALS);
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3_test2() {
        parseJava13(ASSERT_TEST5_a);
    }

    private static final String ANNOTATED_LOCALS =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  @SuppressWarnings(\"foo\") int y = 5;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ANNOTATED_PARAMS =
            "public class Foo {" + PMD.EOL +
            " void bar(@SuppressWarnings(\"foo\") int x) {}" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert x == 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x == 2);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST3 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x==2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST4 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x==2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST5 =
            "public class Foo {" + PMD.EOL +
            "  int assert = 2;" + PMD.EOL +
            "}";


    private static final String ASSERT_TEST5_a =
            "public class Foo {" + PMD.EOL +
            "  void bar() { assert(); }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST6 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  assert (x == 2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST7 =
            "public class Foo {" + PMD.EOL +
            " void assert() {}" + PMD.EOL +
            "}";

    private static final String JDK15_ENUM =
            "public class Test {" + PMD.EOL +
            " enum Season { winter, spring, summer, fall };" + PMD.EOL +
            "}";

    private static final String JDK14_ENUM =
            "public class Test {" + PMD.EOL +
            " int enum;" + PMD.EOL +
            "}";

    private static final String JDK15_VARARGS =
            "public class Test {" + PMD.EOL +
            " void bar(Object ... args) {}" + PMD.EOL +
            "}";

    private static final String JDK15_FORLOOP =
            "public class Test {" + PMD.EOL +
            " void foo(List list) {" + PMD.EOL +
            "  for (Integer i : list) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String JDK15_FORLOOP_WITH_MODIFIER =
            "public class Test {" + PMD.EOL +
            " void foo(List list) {" + PMD.EOL +
            "  for (final Integer i : list) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String JDK15_GENERICS =
            "public class Test {" + PMD.EOL +
            "  ArrayList<Integer> list =  new ArrayList<Integer>();" + PMD.EOL +
            "}";

    private static final String FIELDS_BUG =
            "public class Test {" + PMD.EOL +
            "  private Foo bar;" + PMD.EOL +
            "}";

    private static final String GT_BUG =
            "public class Test {" + PMD.EOL +
            "  int y = x > 32;" + PMD.EOL +
            "}";

    private static final String ANNOTATIONS_BUG =
            "@Target(ElementType.METHOD)" + PMD.EOL +
            "public @interface Foo {" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_FIELD =
            "public class Foo {" + PMD.EOL +
            " Class<Double> foo = (Class<Double>)clazz;" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_ANNOTATION =
            "public class Foo {" + PMD.EOL +
            " public <A extends Annotation> A foo(Class<A> c) {" + PMD.EOL +
            "  return null;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String INNER_BUG =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   final class Inner {};" + PMD.EOL +
            "   Inner i = new Inner();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String INNER_BUG2 =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   class Inner {};" + PMD.EOL +
            "   Inner i = new Inner();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_METHOD_CALL =
            "public class Test {" + PMD.EOL +
            "  List<String> test() {" + PMD.EOL +
            "   return Collections.<String>emptyList();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String GENERIC_RETURN_TYPE =
            "public class Test {" + PMD.EOL +
            "  public static <String> String test(String x) {" + PMD.EOL +
            "   return x;" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    // See java/lang/concurrent/ConcurrentHashMap
    private static final String MULTIPLE_GENERICS =
            "public class Foo<K,V> {" + PMD.EOL +
            "  public <A extends K, B extends V> Foo(Bar<A,B> t) {}" + PMD.EOL +
            "}";

    // See java/lang/concurrent/CopyOnWriteArraySet
    private static final String FUNKY_GENERICS =
            "public class Foo {" + PMD.EOL +
            "  public <T extends E> Foo() {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JDKVersionTest.class);
    }
}
