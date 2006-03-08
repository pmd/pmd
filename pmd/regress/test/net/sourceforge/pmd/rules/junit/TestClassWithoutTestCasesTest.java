/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class TestClassWithoutTestCasesTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("junit", "TestClassWithoutTestCases");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "success case", 0, rule),
            new TestDescriptor(TEST3, "inner class should get checked", 1, rule),
            new TestDescriptor(TEST4, "inner class that's ok ", 0, rule),
            new TestDescriptor(TEST5, "abstract classes are ok", 0, rule),
            new TestDescriptor(TEST6, "ditto interfaces", 0, rule),
        });
    }

    private static final String TEST1 =
    "public class FooTest {}";

    private static final String TEST2 =
    "public class FooTest {" + PMD.EOL +
    "   void testX(){}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class FooTest {" + PMD.EOL +
    "   class Bar { void testX(){} } " + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "   class BarTest { void testX(){} } " + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public abstract class FooTest {" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public interface FooTest {" + PMD.EOL +
    "}";

}
