/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryBooleanAssertionTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("junit", "UnnecessaryBooleanAssertion");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "failure case", 1, rule),
               new TestDescriptor(TEST2, "variations", 4, rule),
               new TestDescriptor(TEST3, "nested boolean literal", 0, rule),
               new TestDescriptor(TEST4, "asserting true a !", 1, rule),
               new TestDescriptor(TEST5, "asserting false a !", 1, rule),
               new TestDescriptor(TEST6, "buz", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertTrue(true); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertTrue(true); " + PMD.EOL +
    "  assertTrue(false); " + PMD.EOL +
    "  assertFalse(true); " + PMD.EOL +
    "  assertFalse(false); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertTrue(foobar(fiddle, true)); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertTrue(!foo); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";


    private static final String TEST5 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertFalse(!foo); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assertTrue(!s.contains(new NameOccurrence(node, node.getImage()))); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
