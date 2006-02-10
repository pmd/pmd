package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SuspiciousEqualsMethodNameRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "SuspiciousEqualsMethodName");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad, equals(Foo foo)", 1, rule),
            new TestDescriptor(TEST2, "ok, equals(Object foo)", 0, rule),
            new TestDescriptor(TEST3, "bad, equal(Object foo)", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(Foo foo) {return true;}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(Object foo) {return true;}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public boolean equal(Object foo) {return true;}" + PMD.EOL +
            "}";

}
