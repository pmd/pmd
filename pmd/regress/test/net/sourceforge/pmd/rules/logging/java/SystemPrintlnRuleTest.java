/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.logging.java;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SystemPrintlnRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/logging-java.xml", "SystemPrintln");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one", 1, rule),
           new TestDescriptor(TEST2, "many", 3, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void foo() { " + PMD.EOL +
    " 	System.out.println(\"debug message\");  " + PMD.EOL +
    " }  " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private void foo1() { " + PMD.EOL +
    " 	System.out.println(\"debug message\");  " + PMD.EOL +
    " }  " + PMD.EOL +
    " private void foo2() { " + PMD.EOL +
    " 	System.out.println(\"debug message\");  " + PMD.EOL +
    " 	System.out.println(\"debug message\");  " + PMD.EOL +
    " }  " + PMD.EOL +
    "}";

}
