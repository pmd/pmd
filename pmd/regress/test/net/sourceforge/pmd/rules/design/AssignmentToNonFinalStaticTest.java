package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AssignmentToNonFinalStaticTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "AssignmentToNonFinalStatic");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "clear rule violation", 1, rule),
            new TestDescriptor(TEST2, "ok", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " static int x;" + PMD.EOL +
            "     Foo(int y) {" +
            "     x = y; " + PMD.EOL +
            "     }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " static final int x;" + PMD.EOL +
            "     Foo(int y) {" +
            "     x = y; " + PMD.EOL +
            "     }" + PMD.EOL +
            "}";

}
