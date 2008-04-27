package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import org.junit.Test;

import java.io.StringReader;

public class JDKVersionTest {

    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        TargetJDKVersion jdk = new TargetJDK1_5();
        JavaParser p = jdk.createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST1)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST2)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST3)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST4)).CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST5)).CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST7)).CompilationUnit();
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST5));
        jp.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith1_3() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST6));
        jp.CompilationUnit();
    }
    // assert keyword/identifier

    @Test
    public void testVarargsShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_VARARGS));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_VARARGS));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_FORLOOP));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_FORLOOP_WITH_MODIFIER));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_FORLOOP));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_GENERICS));
        p.CompilationUnit();
    }

    @Test
    public void testVariousParserBugs() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(FIELDS_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(GT_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(ANNOTATIONS_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(CONSTANT_FIELD_IN_ANNOTATION_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_FIELD));
        p.CompilationUnit();
    }

    @Test
    public void testNestedClassInMethodBug() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(INNER_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(INNER_BUG2));
        p.CompilationUnit();
    }

    @Test
    public void testGenericsInMethodCall() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_METHOD_CALL));
        p.CompilationUnit();
    }

    @Test
    public void testGenericINAnnotation() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_ANNOTATION));
        p.CompilationUnit();
    }

    @Test
    public void testGenericReturnType() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_RETURN_TYPE));
        p.CompilationUnit();
    }

    @Test
    public void testMultipleGenerics() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(FUNKY_GENERICS));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(MULTIPLE_GENERICS));
        p.CompilationUnit();
    }

    @Test
    public void testAnnotatedParams() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(ANNOTATED_PARAMS));
        p.CompilationUnit();
    }

    @Test
    public void testAnnotatedLocals() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(ANNOTATED_LOCALS));
        p.CompilationUnit();
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3_test2() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST5_a));
        jp.CompilationUnit();
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

    private static final String CONSTANT_FIELD_IN_ANNOTATION_BUG =
            "public @interface Foo {" + PMD.EOL +
            "  String CONST = \"foo\";" + PMD.EOL +
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
