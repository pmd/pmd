package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptySwitchStmtRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[count(*) = 1]");
    }

    public void test1() throws Throwable {
        runTestFromFile("EmptySwitchStmt1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("EmptySwitchStmt2.java", 0, rule);
    }
}
