/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseAssertEqualsInsteadOfAssertTrueTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("junit", "UseAssertEqualsInsteadOfAssertTrue");
    }

    public void testAll() throws Throwable {

        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 0, rule),
            new TestDescriptor(TEST2, "TEST2", 1, rule),
            new TestDescriptor(TEST3, "TEST3", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertTrue(a==b);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertTrue(a.equals(b));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertTrue(a.mySpecialequals(b));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
