package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.XPathRule;

public class UnconditionalIfStatementRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement/Expression/ConditionalAndExpression/InstanceOfExpression/UnaryExpression/PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "if (true)", 1, rule),
           new TestDescriptor(TEST2, "if (false)", 1, rule),
           new TestDescriptor(TEST3, "no constant folding", 0, rule),
           new TestDescriptor(TEST4, "short circuit operator", 1, rule)
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

    // FIXME - this shouldn't be flagged
    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x, boolean y) {" + PMD.EOL +
    "  if (y == true) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
