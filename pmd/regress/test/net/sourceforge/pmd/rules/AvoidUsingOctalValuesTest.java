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
            new TestDescriptor(TEST4, "OK, double value", 0, rule),
            new TestDescriptor(TEST5, "OK, double value", 0, rule),
            new TestDescriptor(TEST6, "bad, 012L", 1, rule),
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

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "  double d = 0.1;" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    "  float f = 0f;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    "  long x = 012L;" + PMD.EOL +
    "}";

}
