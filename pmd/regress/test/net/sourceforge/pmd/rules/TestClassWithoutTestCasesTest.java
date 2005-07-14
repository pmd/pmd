/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

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
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "TEST1", 1, rule),
		   new TestDescriptor(TEST2, "TEST2", 0, rule),
		   new TestDescriptor(TEST3, "TEST3", 1, rule),
		   new TestDescriptor(TEST4, "TEST4", 0, rule),
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

}
