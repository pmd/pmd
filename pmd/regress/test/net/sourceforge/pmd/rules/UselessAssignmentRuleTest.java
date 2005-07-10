package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UselessAssignmentRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/scratchpad.xml", "UselessAssignment");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               //new TestDescriptor(TEST1, "useless assignment", 0, rule),
       });
    }


    private static final String TEST1 =
    "public class Bar {" + PMD.EOL +
    " void foo(){" + PMD.EOL +
    "  //int x = 2; " + PMD.EOL +
    "  //foo(x); " + PMD.EOL +
    "  //x = 5; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
