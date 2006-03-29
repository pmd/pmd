package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidInstanceofChecksInCatchClauseTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "AvoidInstanceofChecksInCatchClause");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad, instanceof FooException", 1, rule),
            new TestDescriptor(TEST2, "ok, no instanceof", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" +
            "  try {" + PMD.EOL +
            "   foo();" + PMD.EOL +
            "  } catch (Exception e) {" + PMD.EOL +
            "   if (e instanceof FooException) {}" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" +
            "  try {" + PMD.EOL +
            "   foo();" + PMD.EOL +
            "  } catch (Exception e) {" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
