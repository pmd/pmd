/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryBooleanAssertionTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/newrules.xml", "UnnecessaryBooleanAssertion");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 4, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo extends TestCase {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "    assertTrue(true); " + PMD.EOL +
    "}" + PMD.EOL +
    "}";

    private static final String TEST2 =
        "public class Foo extends TestCase {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "    assertTrue(true); " + PMD.EOL +
        "    assertTrue(false); " + PMD.EOL +
        "    assertFalse(true); " + PMD.EOL +
        "    assertFalse(false); " + PMD.EOL +
        "}" + PMD.EOL +
        "}";

}
