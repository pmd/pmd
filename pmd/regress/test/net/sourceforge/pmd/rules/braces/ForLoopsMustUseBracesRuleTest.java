/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ForLoopsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/braces.xml", "ForLoopsMustUseBracesRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "", 1, rule),
           new TestDescriptor(TEST4, "", 1, rule),
           new TestDescriptor(TEST5, "", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  for (int i=0; i<42;i++)" + PMD.EOL +
    "       foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  for (int i=0; i<42;i++) {" + PMD.EOL +
    "       foo();" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  for (int i=0; i<42;) " + PMD.EOL +
    "       foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  for (int i=0;;) " + PMD.EOL +
    "       foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  for (;;) " + PMD.EOL +
    "       foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
