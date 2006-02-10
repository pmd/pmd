/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class DoubleCheckedLockingTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "DoubleCheckedLocking");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "simple ok", 0, rule),
            new TestDescriptor(TEST2, "simple failure", 1, rule),
            new TestDescriptor(TEST3, "skip interfaces", 0, rule),
        });
    }

    public void testGenerics() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST4, rule, rpt);
        assertEquals(0, rpt.size());
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "      Object baz;" + PMD.EOL +
            "      Object bar() {" + PMD.EOL +
            "        if(baz == null) { //baz may be non-null yet not fully created" + PMD.EOL +
            "          synchronized(this){" + PMD.EOL +
            "            if(baz == null){" + PMD.EOL +
            "              baz = new Object();" + PMD.EOL +
            "            }" + PMD.EOL +
            "          }" + PMD.EOL +
            "        }" + PMD.EOL +
            "        return baz;" + PMD.EOL +
            "      }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public interface Foo {" + PMD.EOL +
            " void foo();" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public <T> Bar<T> foo() { /* blah */}" + PMD.EOL +
            "}";
}
