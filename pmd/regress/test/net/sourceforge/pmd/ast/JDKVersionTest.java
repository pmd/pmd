package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import java.io.StringReader;

public class JDKVersionTest extends TestCase  {

    // enum keyword/identifier
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        try {
            JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_ENUM));
            p.CompilationUnit();
            throw new Error("JDK 1.4 parser should have failed to parse enum used as keyword");
        } catch (ParseException e) {}    // cool
    }

    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }

    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        try {
            TargetJDKVersion jdk = new TargetJDK1_5();
            JavaParser p = jdk.createParser(new StringReader(JDK14_ENUM));
            p.CompilationUnit();
            throw new Error("JDK 1.5 parser should have failed to parse enum used as identifier");
        } catch (ParseException e) {}    // cool
    }
    // enum keyword/identifier

    // assert keyword/identifier
    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST1)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST2)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST3)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST4)).CompilationUnit();
    }

    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        try {
            (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST5)).CompilationUnit();
            throw new RuntimeException("Usage of assert as identifier should have failed with 1.4");
        } catch (ParseException pe) {
            // cool
        }
    }

    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        try {
            (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST7)).CompilationUnit();
            throw new RuntimeException("Usage of assert as identifier should have failed with 1.4");
        } catch (ParseException pe) {
            // cool
        }
    }

    public void testAssertAsIdentifierSucceedsWith1_3() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST5));
        jp.CompilationUnit();
    }

    public void testAssertAsKeywordFailsWith1_3() {
        try {
            JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST6));
            jp.CompilationUnit();
            throw new RuntimeException("Usage of assert as keyword should have failed with 1.3");
        } catch (ParseException pe) {
            // cool
        }
    }
    // assert keyword/identifier

    private static final String ASSERT_TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String ASSERT_TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String ASSERT_TEST3 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2 : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String ASSERT_TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2) : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String ASSERT_TEST5 =
    "public class Foo {" + PMD.EOL +
    "  int assert = 2;" + PMD.EOL +
    "}";

    private static final String ASSERT_TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  assert (x>2) : \"hi!\";" + PMD.EOL +
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
}
