package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptySwitchStmtRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class EmptySwitchStmt1 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  switch (x) {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptySwitchStmt2 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  switch (x) {" + CPD.EOL +
    "   case 2: int y=4;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[count(*) = 1]");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
