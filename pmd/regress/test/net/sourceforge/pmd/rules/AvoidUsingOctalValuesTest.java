package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidUsingOctalValuesTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "AvoidUsingOctalValues");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad, 012", 1, rule),
            new TestDescriptor(TEST2, "OK, hex value", 0, rule),
            new TestDescriptor(TEST3, "OK, long value", 0, rule),
        });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "  int x = 012;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "  int x = 0xCAFE;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "  long x = 0L;" + PMD.EOL +
    "}";

}
