package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class IfElseStmtsMustUseBracesRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class IfElseStmtsNeedBraces1 {" + CPD.EOL +
    "       public void foo() {     " + CPD.EOL +
    "               int x =0;" + CPD.EOL +
    "               if (true == true) " + CPD.EOL +
    "                       x=2;" + CPD.EOL +
    "                else " + CPD.EOL +
    "                       x=4;" + CPD.EOL +
    "               " + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class IfElseStmtsNeedBraces2 {" + CPD.EOL +
    "       public void foo() {     " + CPD.EOL +
    "               int x =0;" + CPD.EOL +
    "               if (true == true) {" + CPD.EOL +
    "                       x=2;" + CPD.EOL +
    "               } else {" + CPD.EOL +
    "                       x=4;" + CPD.EOL +
    "               }" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";



    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) > 2][not(Statement/Block)]");
    }

    public void testIfElseStmtsMustUseBraces1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testIfElseStmtsMustUseBraces2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
