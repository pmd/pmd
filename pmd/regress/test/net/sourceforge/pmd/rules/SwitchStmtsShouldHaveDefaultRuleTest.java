package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class SwitchStmtsShouldHaveDefaultRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class SwitchStmtsShouldHaveDefault1 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  switch (x) {" + CPD.EOL +
    "   case 2: int y=8;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class SwitchStmtsShouldHaveDefault2 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  switch (x) {" + CPD.EOL +
    "   case 2: int y=8;" + CPD.EOL +
    "   default: int j=8;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[not(SwitchLabel[count(*) = 0])]");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }

}
