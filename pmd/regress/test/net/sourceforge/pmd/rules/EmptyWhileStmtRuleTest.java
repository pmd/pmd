package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyWhileStmtRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class EmptyWhileStmtRule {" + CPD.EOL +
    "    public EmptyWhileStmtRule() {" + CPD.EOL +
    "       while (true == true) {" + CPD.EOL +
    "       }" + CPD.EOL +
    "       while (true == true) {" + CPD.EOL +
    "               String x = \"\";" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement/Statement/Block[count(*) = 0]");
    }

    public void testEmptyWhileStmtRule() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }


}
