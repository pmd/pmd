package test.net.sourceforge.pmd.rules.migrating;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AvoidAssertAsIdentifierTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("migrating", "AvoidAssertAsIdentifier");
    }

    public void testOne() throws Throwable {
        Report rpt = new Report();
        super.runTestFromString13(TEST1, rule, rpt);
        assertEquals(2, rpt.size());
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar(String assert) {" + PMD.EOL +
    "  String assert = \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
