package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyIfStmtRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement/Statement/Block[count(*) = 0]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one empty, one not empty", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class EmptyIfStmtRule {" + CPD.EOL +
    "    public EmptyIfStmtRule() {" + CPD.EOL +
    "       if (null == null) {" + CPD.EOL +
    "       }" + CPD.EOL +
    "       if (null != null) {" + CPD.EOL +
    "               this.toString();" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";


}
