/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.logging.java;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SystemPrintlnTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("logging-java", "SystemPrintln");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "one", 1, rule),
            new TestDescriptor(TEST2, "many", 3, rule),
            new TestDescriptor(TEST3, "none", 0, rule),
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

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " private void foo() { " + PMD.EOL +
            " 	int x = 2;  " + PMD.EOL +
            " }  " + PMD.EOL +
            "}";

}
