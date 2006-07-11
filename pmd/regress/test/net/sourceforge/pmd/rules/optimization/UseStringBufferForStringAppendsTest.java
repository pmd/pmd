package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseStringBufferForStringAppendsTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "UseStringBufferForStringAppends");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "concat inside method call", 0, rule),
            new TestDescriptor(TEST3, "3, startsWith", 0, rule),
            new TestDescriptor(TEST4, "4, compound append, should only report 1 failure", 1, rule),
            //new TestDescriptor(TEST5, "5, failure case", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  String x;" + PMD.EOL +
            "  x = \"foo\";" + PMD.EOL +
            "  x += \"bar\";" + PMD.EOL +
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
        " public void bar() {" + PMD.EOL +
        "    foo(\"abc\" + def + \"hij\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void bar() {" + PMD.EOL +
        "  String x;" + PMD.EOL +
        "  x = \"foo\";" + PMD.EOL +
        "  x += \"bar\" + x;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public Foo() {" + PMD.EOL +
        "  String x;" + PMD.EOL +
        "  x = \"foo\";" + PMD.EOL +
        "  x += \"bar\";" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
}
