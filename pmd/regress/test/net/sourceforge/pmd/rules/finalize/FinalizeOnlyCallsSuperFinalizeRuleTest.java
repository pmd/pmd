package test.net.sourceforge.pmd.rules.finalize;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class FinalizeOnlyCallsSuperFinalizeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/finalizers.xml", "FinalizeOnlyCallsSuperFinalize");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void finalize() {" + PMD.EOL +
    "  super.finalize();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void finalize() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  super.finalize();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
