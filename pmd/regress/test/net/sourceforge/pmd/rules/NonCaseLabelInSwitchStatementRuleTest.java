package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NonCaseLabelInSwitchStatementRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "NonCaseLabelInSwitchStatement");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "label inside switch", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar(int x) {" + PMD.EOL +
    "  switch (x) {" + PMD.EOL +
    "   case 2: int y=8;" + PMD.EOL +
    "    break;" + PMD.EOL +
    "   somelabel: " + PMD.EOL +
    "    break;" + PMD.EOL +
    "   default: " + PMD.EOL +
    "    int j=8;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar(int x) {" + PMD.EOL +
    "  switch (x) {" + PMD.EOL +
    "   case 2: int y=8;" + PMD.EOL +
    "    break;" + PMD.EOL +
    "   default: " + PMD.EOL +
    "    int j=8;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
