package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnconditionalIfStatementRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "UnconditionalIfStatement");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "if (true)", 1, rule),
           new TestDescriptor(TEST2, "if (false)", 1, rule),
           new TestDescriptor(TEST3, "no constant folding", 0, rule),
           new TestDescriptor(TEST4, "short circuit operator", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  if (true) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  if (false) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private static final boolean DEBUG = \"false\";" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  if (DEBUG) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x, boolean y) {" + PMD.EOL +
    "  if (y == true) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
