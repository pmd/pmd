package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyIfStmtRuleTest extends RuleTst {

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


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement/Statement/Block[count(*) = 0]");
    }

    public void testOneEmptyOneNotEmpty() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

}
