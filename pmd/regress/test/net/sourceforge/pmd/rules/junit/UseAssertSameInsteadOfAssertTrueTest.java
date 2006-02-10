/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseAssertSameInsteadOfAssertTrueTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("junit", "UseAssertSameInsteadOfAssertTrue");
    }

    public void testAll() throws Throwable {

        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "assert true a == b", 1, rule),
            new TestDescriptor(TEST2, "assert true a != b", 1, rule),
            new TestDescriptor(TEST3, "assert false a == b", 1, rule),
            new TestDescriptor(TEST4, "assert false a != b", 1, rule),
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
            "  assertTrue(a!=b);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertFalse(a==b);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertFalse(a!=b);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
