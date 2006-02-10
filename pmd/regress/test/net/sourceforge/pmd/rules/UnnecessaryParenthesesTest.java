package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryParenthesesTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("controversial", "UnnecessaryParentheses");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case, returning literal", 1, rule),
            new TestDescriptor(TEST2, "ok, complex expression", 0, rule),
            new TestDescriptor(TEST3, "bad, returning variable in parens", 1, rule),
            new TestDescriptor(TEST4, "ok, returning comparison", 0, rule),
        });
    }

    private static final String TEST1 =
            " public class Foo {" + PMD.EOL +
            "  int bar() {" + PMD.EOL +
            "   return (2); " + PMD.EOL +
            "  }" + PMD.EOL +
            " }";

    private static final String TEST2 =
            " public class Foo {" + PMD.EOL +
            "  int bar() {" + PMD.EOL +
            "   return (2+2); " + PMD.EOL +
            "  }" + PMD.EOL +
            " }";

    private static final String TEST3 =
            " public class Foo {" + PMD.EOL +
            "  int bar(int y) {" + PMD.EOL +
            "   return (y); " + PMD.EOL +
            "  }" + PMD.EOL +
            " }";

    private static final String TEST4 =
            " public class Foo {" + PMD.EOL +
            "  int bar(int y) {" + PMD.EOL +
            "   return (x=y); " + PMD.EOL +
            "  }" + PMD.EOL +
            " }";

}
