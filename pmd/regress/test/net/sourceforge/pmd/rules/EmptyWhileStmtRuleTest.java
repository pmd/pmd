package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyWhileStmtRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement/Statement/Block[count(*) = 0]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    "    public void bar() {" + CPD.EOL +
    "       while (true == true) {" + CPD.EOL +
    "       }" + CPD.EOL +
    "       while (true == true) {" + CPD.EOL +
    "               String x = \"\";" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

}
