/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class LongVariableRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/naming.xml", "LongVariable");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "param", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "local", 1, rule),
           new TestDescriptor(TEST4, "field", 1, rule),
           new TestDescriptor(TEST5, "for", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "    void foo(String argsWithExtraMustard) {} " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "    void foo() {" + PMD.EOL +
    "       int bugleDeWump = -1;" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "" + PMD.EOL +
    "    void foo() {" + PMD.EOL +
    "       int abcdefghijklmnopqrstuvwxyz = -1; " + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "   void foo() {" + PMD.EOL +
    "       for (int interestingIntIterator = 0; " + PMD.EOL +
    "            interestingIntIterator < 10; " + PMD.EOL +
    "            interestingIntIterator++) { }" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    "    private int abcdefghijklmnopqrstuvwxyz;" + PMD.EOL +
    "}";

}
