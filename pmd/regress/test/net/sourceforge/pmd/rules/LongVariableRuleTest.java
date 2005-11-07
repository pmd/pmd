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
        rule = findRule("naming", "LongVariable");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "param", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "local", 1, rule),
           new TestDescriptor(TEST4, "for", 1, rule),
           new TestDescriptor(TEST5, "17 character max", 1, rule),
       });
    }

    public void testThreshold() {
        rule.addProperty("minimum", "3");
        runTests(new TestDescriptor[] {
            new TestDescriptor(TEST6, "threshold test", 2, rule),
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
    "    private int a2345678901234567;" + PMD.EOL +
    "    private int a23456789012345678;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    "    private int a234;" + PMD.EOL +
    "    private int b234;" + PMD.EOL +
    "}";

}
