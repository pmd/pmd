package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class BooleanInversionTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "BooleanInversion");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure, unary assignment", 1, rule),
            new TestDescriptor(TEST2, "pass, bitwise assignment", 0, rule),
            new TestDescriptor(TEST3, "pass, not a straight unary inversion", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "   boolean b = false;" + PMD.EOL +
            "   boolean c = false;" + PMD.EOL +
            "   b = !c;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "   boolean b = false;" + PMD.EOL +
            "   boolean c = false;" + PMD.EOL +
            "   b ^= c;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "   boolean b = false;" + PMD.EOL +
            "   boolean c = false;" + PMD.EOL +
            "   b &= !c;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
