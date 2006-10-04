/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CallSuperInConstructorTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("controversial", "CallSuperInConstructor");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 0, rule),
            new TestDescriptor(TEST2, "TEST2", 0, rule),
            new TestDescriptor(TEST3, "don't flag classes w/o extends", 0, rule),
        });
    }

    public void testEnum() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST4, rule, rpt);
        assertTrue(rpt.isEmpty());
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            " super();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public Foo(Object o) {" + PMD.EOL +
            " 	this();" + PMD.EOL +
            "}" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            " super();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  int x = 2;" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Bar extends Buz {" + PMD.EOL +
            " public static enum Foo {" + PMD.EOL +
            "  ;" + PMD.EOL +
            "  public Foo() {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
