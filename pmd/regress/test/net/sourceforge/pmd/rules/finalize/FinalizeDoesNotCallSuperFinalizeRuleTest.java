package test.net.sourceforge.pmd.rules.finalize;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class FinalizeDoesNotCallSuperFinalizeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("finalizers", "FinalizeDoesNotCallSuperFinalize");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad", 1, rule),
            new TestDescriptor(TEST2, "ok", 0, rule),
            new TestDescriptor(TEST3, "ok, super.finalize called in try..finally", 0, rule),
            new TestDescriptor(TEST4, "ok, super.finalize called in try..catch..finally", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void finalize() {" + PMD.EOL +
            "  super.finalize();" + PMD.EOL +
            "  int x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void finalize() {" + PMD.EOL +
            "  int x = 2;" + PMD.EOL +
            "  super.finalize();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void finalize() {" + PMD.EOL +
            "  try {} finally {" + PMD.EOL +
            "   super.finalize();" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void finalize() {" + PMD.EOL +
            "  try {} catch(Exception e) {} finally {" + PMD.EOL +
            "   super.finalize();" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
