package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimplifyStartsWithTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "SimplifyStartsWith");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "startsWith multiple chars", 0, rule),
            new TestDescriptor(TEST3, "startsWith defined on some other class, doesn't take a String", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public boolean bar(String x) {" + PMD.EOL +
            "  return x.startsWith(\"x\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public boolean bar(Fiddle x) {" + PMD.EOL +
            "  return x.startsWith(\"abc\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public boolean bar(Fiddle x) {" + PMD.EOL +
            "  return x.startsWith(123);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
