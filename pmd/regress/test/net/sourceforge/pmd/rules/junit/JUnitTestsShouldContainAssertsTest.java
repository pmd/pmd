/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.junit.JUnitTestsShouldContainAsserts;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class JUnitTestsShouldContainAssertsTest extends SimpleAggregatorTst {

    public void testAll() throws Throwable {

       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Contains assert", 0, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST2, "Missing assert", 1, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST3, "All ok", 0, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST4, "Two wrong", 2, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST5, "Contains fail", 0, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST6, "One wrong", 1, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST7, "Skip interfaces", 0, new JUnitTestsShouldContainAsserts()),
           new TestDescriptor(TEST8, "Skip abstract methods", 0, new JUnitTestsShouldContainAsserts()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertEquals(\"1 == 1\", 1, 1);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void setUp() {" + PMD.EOL +
    " }" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertTrue(\"foo\", \"foo\".equals(\"foo\"));" + PMD.EOL +
    " }" + PMD.EOL +
    " public void test2() {" + PMD.EOL +
    "  assertEquals(\"foo\", \"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void setUp() {" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        " 	int a;" + PMD.EOL +
        " 	callMethod(a);" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test2() {" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  fail(\"1 == 1\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void setUp() {" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        " 	int a;" + PMD.EOL +
        " 	callMethod(a);" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test2() {" + PMD.EOL +
        " 	fail();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public interface Foo {" + PMD.EOL +
        " public void setUp() throws Exception;" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public abstract void setUp() throws Exception;" + PMD.EOL +
        "}";

}
