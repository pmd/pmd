package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class DefaultLabelNotLastInSwitchStmtRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "DefaultLabelNotLastInSwitchStmt");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, rule),
           new TestDescriptor(TEST2, "bad", 1, rule),
           new TestDescriptor(TEST3, "ok, no default", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar(int x) {" + PMD.EOL +
    "  switch(x) { " + PMD.EOL +
    "  case 1: " + PMD.EOL +
    "   break; " + PMD.EOL +
    "  default:" + PMD.EOL +
    "   break;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar(int x) {" + PMD.EOL +
    "  switch(x) { " + PMD.EOL +
    "  case 1: " + PMD.EOL +
    "   break; " + PMD.EOL +
    "  default:" + PMD.EOL +
    "   break;" + PMD.EOL +
    "  case 2: " + PMD.EOL +
    "   break; " + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void bar(int x) {" + PMD.EOL +
    "  switch(x) { " + PMD.EOL +
    "  case 1: " + PMD.EOL +
    "   break; " + PMD.EOL +
    "  case 2: " + PMD.EOL +
    "   break; " + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
