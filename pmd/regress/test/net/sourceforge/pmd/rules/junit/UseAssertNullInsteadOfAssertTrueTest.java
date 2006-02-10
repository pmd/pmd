package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseAssertNullInsteadOfAssertTrueTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("junit", "UseAssertNullInsteadOfAssertTrue");
    }

    public void testAll() throws Throwable {

        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "assertTrue with null", 1, rule),
            new TestDescriptor(TEST2, "assertFalse with != null", 1, rule),
            new TestDescriptor(TEST3, "assertTrue with x == y", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertTrue(a==null);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertFalse(a != null);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "  assertTrue(a == b);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}

